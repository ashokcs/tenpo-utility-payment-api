package cl.multipay.utility.payments.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.EftCreateOrderResponse;
import cl.multipay.utility.payments.dto.MulticajaBill;
import cl.multipay.utility.payments.dto.UtilityPaymentTransactionPay;
import cl.multipay.utility.payments.dto.UtilityPaymentTransactionRequest;
import cl.multipay.utility.payments.dto.WebpayInitResponse;
import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentEft;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.http.EftClient;
import cl.multipay.utility.payments.http.UtilityPaymentClient;
import cl.multipay.utility.payments.http.WebpayClient;
import cl.multipay.utility.payments.service.UtilityPaymentBillService;
import cl.multipay.utility.payments.service.UtilityPaymentEftService;
import cl.multipay.utility.payments.service.UtilityPaymentTransactionService;
import cl.multipay.utility.payments.service.UtilityPaymentWebpayService;
import cl.multipay.utility.payments.util.Utils;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilityPaymentTransactionController
{
	private final UtilityPaymentTransactionService utilityPaymentTransactionService;
	private final UtilityPaymentBillService utilityPaymentBillService;
	private final UtilityPaymentWebpayService utilityPaymentWebpayService;
	private final UtilityPaymentEftService utilityPaymentEftService;

	private final WebpayClient webpayClient;
	private final EftClient eftClient;
	private final UtilityPaymentClient utilityPaymentClient;

	public UtilityPaymentTransactionController(final UtilityPaymentTransactionService utilityPaymentTransactionService,
		final UtilityPaymentWebpayService utilityPaymentWebpayService,
		final UtilityPaymentEftService utilityPaymentEftService, final UtilityPaymentBillService utilityPaymentBillService,
		final UtilityPaymentClient utilityPaymentClient, final WebpayClient webpayClient, final EftClient eftClient)
	{
		this.utilityPaymentTransactionService = utilityPaymentTransactionService;
		this.utilityPaymentWebpayService = utilityPaymentWebpayService;
		this.utilityPaymentClient = utilityPaymentClient;
		this.utilityPaymentBillService = utilityPaymentBillService;
		this.webpayClient = webpayClient;
		this.eftClient = eftClient;
		this.utilityPaymentEftService = utilityPaymentEftService;
	}

	/**
	 * Obtiene los detalles de una transacción.
	 *
	 * @param publicId Identificador público de la cuenta
	 * @return Los datos de la cuenta
	 */
	@GetMapping("/v1/transactions/{id:^[0-9a-f]{32}$}")
	public ResponseEntity<UtilityPaymentTransaction> get(@PathVariable("id") final String publicId)
	{
		final UtilityPaymentTransaction upt = utilityPaymentTransactionService.findByPublicId(publicId).orElseThrow(NotFoundException::new);
		return ResponseEntity.ok(upt);
	}

	/**
	 *	Consulta la deuda de una cuenta y crea una transacción.
	 *
	 * @param request El identificador del servicio y cuenta a consultar
	 * @return Los datos de la cuenta
	 */
	@PostMapping("/v1/transactions")
	public ResponseEntity<UtilityPaymentTransaction> create(@RequestBody @Valid final UtilityPaymentTransactionRequest request)
	{
		// TODO validate identifier

		// get utility bill
		final String utility = request.getUtility();
		final String collector = request.getCollector();
		final String identifier = request.getIdentifier();
		final Optional<MulticajaBill> billDetailsOptional = utilityPaymentClient.getBill(utility, identifier, collector);

		// if not present return 204 no content
		if (!billDetailsOptional.isPresent()) {
			return ResponseEntity.noContent().build();
		}

		// get response
		final MulticajaBill multicajaBill = billDetailsOptional.get();
		final String mcCode = multicajaBill.getMcCode();
		final Long amount = multicajaBill.getAmount();
		final String dueDate = multicajaBill.getDueDate();

		// create utility payment transaction
		final UtilityPaymentTransaction utilityPaymentTransaction = new UtilityPaymentTransaction();
		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.PENDING);
		utilityPaymentTransaction.setPublicId(Utils.uuid());
		utilityPaymentTransaction.setAmount(amount);
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction)
			.orElseThrow(ServerErrorException::new);

		// create utility payment bill
		// TODO save with relationship
		final UtilityPaymentBill utilityPaymentBill = new UtilityPaymentBill();
		utilityPaymentBill.setStatus(UtilityPaymentBill.PENDING);
		utilityPaymentBill.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentBill.setUtility(utility);
		utilityPaymentBill.setCollector(collector);
		utilityPaymentBill.setIdentifier(identifier);
		utilityPaymentBill.setMcCode(mcCode);
		utilityPaymentBill.setAmount(amount);
		utilityPaymentBill.setDueDate(dueDate);
		utilityPaymentBillService.saveAndRefresh(utilityPaymentBill)
			.orElseThrow(ServerErrorException::new);

		// return utility payment transaction
		return ResponseEntity.status(HttpStatus.CREATED).body(utilityPaymentTransaction); // TODO incluir detalles
	}

	/**
	 * Inicializa el pago de una transacción mediante webpay.
	 *
	 * @param publicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/transactions/{id:^[0-9a-f]{32}$}/webpay")
	public ResponseEntity<UtilityPaymentWebpay> utilityPaymentWebpay(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final UtilityPaymentTransactionPay utilityPaymentTransactionPay
	) {
		// get utility payment transaction by id and status
		final UtilityPaymentTransaction utilityPaymentTransaction = utilityPaymentTransactionService.getPendingByPublicId(publicId)
				.orElseThrow(NotFoundException::new);

		// webpay init payment
		final WebpayInitResponse webpayInitResponse = webpayClient.init(utilityPaymentTransaction)
				.orElseThrow(ServerErrorException::new);

		// save webpay response
		final UtilityPaymentWebpay utilityPaymentWebpay = new UtilityPaymentWebpay();
		utilityPaymentWebpay.setStatus(UtilityPaymentWebpay.PENDING);
		utilityPaymentWebpay.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentWebpay.setToken(webpayInitResponse.getToken());
		utilityPaymentWebpay.setUrl(webpayInitResponse.getUrl());
		utilityPaymentWebpayService.save(utilityPaymentWebpay).orElseThrow(ServerErrorException::new);

		// update utility payment transaction status and email
		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(UtilityPaymentTransaction.WEBPAY);
		utilityPaymentTransaction.setEmail(utilityPaymentTransactionPay.getEmail());
		utilityPaymentTransactionService.save(utilityPaymentTransaction).orElseThrow(ServerErrorException::new);

		// return redirect url
		return ResponseEntity.ok(utilityPaymentWebpay);
	}

	/**
	 * Inicializa el pago de una transacción mediante transferencia.
	 *
	 * @param publicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/transactions/{id:^[0-9a-f]{32}$}/eft")
	public ResponseEntity<UtilityPaymentEft> payTef(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final UtilityPaymentTransactionPay utilityPaymentTransactionPay
	) {
		// get utility payment transaction by id and status
		final String tefPublicId = Utils.uuid();
		final String tefNotifyId = Utils.uuid();
		final UtilityPaymentTransaction utilityPaymentTransaction = utilityPaymentTransactionService.getPendingByPublicId(publicId)
				.orElseThrow(NotFoundException::new);

		// eft create order
		final EftCreateOrderResponse tefResponse = eftClient.createOrder(utilityPaymentTransaction, tefPublicId, tefNotifyId)
				.orElseThrow(ServerErrorException::new);

		// save utility payment eft response
		final UtilityPaymentEft utilityPaymentEft = new UtilityPaymentEft();
		utilityPaymentEft.setStatus(UtilityPaymentEft.PENDING);
		utilityPaymentEft.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentEft.setPublicId(tefPublicId);
		utilityPaymentEft.setNotifyId(tefNotifyId);
		utilityPaymentEft.setOrderId(tefResponse.getMcOrderId());
		utilityPaymentEft.setUrl(tefResponse.getRedirectUrl());
		utilityPaymentEftService.save(utilityPaymentEft).orElseThrow(ServerErrorException::new);

		// update utility payment transaction status and email
		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(UtilityPaymentTransaction.EFT);
		utilityPaymentTransaction.setEmail(utilityPaymentTransactionPay.getEmail());
		utilityPaymentTransactionService.save(utilityPaymentTransaction).orElseThrow(ServerErrorException::new);

		// return redirect url
		return ResponseEntity.ok(utilityPaymentEft);
	}
	// TODO Subir azure
	// TODO migrations
	// TODO create tabla padre
	// TODO service entry sendgrid
	// TODO check time on azure db, gmt?
	// TODO bo totalizadores
	// TODO docker file staging production env

	// TODO modificar api readme new version
	// TODO probar eft dev mc
	// TODO html correo comprobante
	// TODO dejar host db parameter en properties
	// TODO rename properties redirect to front
	// TODO backup database
	// TODO mover archivos de ambientes a carpetas, env
	// TODO bo totalizadores task correccion
}