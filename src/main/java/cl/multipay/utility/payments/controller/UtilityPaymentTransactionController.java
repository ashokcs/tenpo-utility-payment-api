package cl.multipay.utility.payments.controller;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.MDC;
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
import cl.multipay.utility.payments.dto.MulticajaBillResponse;
import cl.multipay.utility.payments.dto.UtilityPaymentEftResponse;
import cl.multipay.utility.payments.dto.UtilityPaymentTransactionPay;
import cl.multipay.utility.payments.dto.UtilityPaymentTransactionRequest;
import cl.multipay.utility.payments.dto.UtilityPaymentTransactionResponse;
import cl.multipay.utility.payments.dto.UtilityPaymentWebpayResponse;
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
	private final Utils utils;

	public UtilityPaymentTransactionController(final UtilityPaymentTransactionService utilityPaymentTransactionService,
		final UtilityPaymentWebpayService utilityPaymentWebpayService, final Utils utils,
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
		this.utils = utils;
	}

	/**
	 * Obtiene los detalles de una transacción.
	 *
	 * @param publicId Identificador público de la cuenta
	 * @return Los datos de la cuenta
	 */
	@GetMapping("/v1/transactions/{id:[0-9a-f]{32}}")
	public ResponseEntity<UtilityPaymentTransactionResponse> get(@PathVariable("id") final String publicId)
	{
		MDC.put("transaction", utils.mdc(publicId));

		final UtilityPaymentTransaction upt = utilityPaymentTransactionService.findByPublicId(publicId).orElseThrow(NotFoundException::new);
		final UtilityPaymentBill upb = utilityPaymentBillService.findByTransactionId(upt.getId()).orElseThrow(NotFoundException::new);

		final UtilityPaymentTransactionResponse uptr = new UtilityPaymentTransactionResponse(upt, upb);

		if (upt.getPaymentMethod() != null) {
			switch (upt.getPaymentMethod()) {

			case UtilityPaymentTransaction.WEBPAY:
				final Optional<UtilityPaymentWebpay> upw = utilityPaymentWebpayService.findByTransactionId(upt.getId());
				if (upw.isPresent()) uptr.setWebpay(upw.get());
				break;

			case UtilityPaymentTransaction.EFT:
				final Optional<UtilityPaymentEft> upe = utilityPaymentEftService.findByTransactionId(upt.getId());
				if (upe.isPresent()) uptr.setEft(upe.get());
				break;
			}
		}

		return ResponseEntity.ok(uptr);
	}

	/**
	 *	Consulta la deuda de una cuenta y crea una transacción.
	 *
	 * @param request El identificador del servicio y cuenta a consultar
	 * @return Los datos de la cuenta
	 */
	@PostMapping("/v1/transactions")
	public ResponseEntity<UtilityPaymentTransactionResponse> create(@RequestBody @Valid final UtilityPaymentTransactionRequest request)
	{
		// create transaction uuid
		final String transactionPublicId = utils.uuid();
		MDC.put("transaction", utils.mdc(transactionPublicId));

		// TODO validate identifier

		// get utility bill
		final String utility = request.getUtility();
		final String collector = request.getCollector();
		final String category = request.getCategory();
		final String identifier = request.getIdentifier();
		final Optional<MulticajaBillResponse> billDetailsOptional = utilityPaymentClient.getBill(utility, identifier, collector);

		// if not present return 204 no content
		if (!billDetailsOptional.isPresent()) {
			return ResponseEntity.noContent().build();
		}

		// get response
		final MulticajaBillResponse multicajaBill = billDetailsOptional.get();
		final Long dataId = multicajaBill.getDebtDataId();
		final Integer debtNumber = multicajaBill.getDebtNumber();
		final String mcCode = multicajaBill.getMcCode();
		final Long amount = multicajaBill.getAmount();
		final String dueDate = multicajaBill.getDueDate();

		// create utility payment transaction
		final UtilityPaymentTransaction utilityPaymentTransaction = new UtilityPaymentTransaction();
		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.PENDING);
		utilityPaymentTransaction.setPublicId(transactionPublicId);
		utilityPaymentTransaction.setAmount(amount);
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction)
			.orElseThrow(ServerErrorException::new);

		// create utility payment bill
		final UtilityPaymentBill utilityPaymentBill = new UtilityPaymentBill();
		utilityPaymentBill.setStatus(UtilityPaymentBill.PENDING);
		utilityPaymentBill.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentBill.setUtility(utility);
		utilityPaymentBill.setCollector(collector);
		utilityPaymentBill.setCategory(category);
		utilityPaymentBill.setIdentifier(identifier);
		utilityPaymentBill.setDataId(dataId);
		utilityPaymentBill.setNumber(debtNumber);
		utilityPaymentBill.setMcCode1(mcCode);
		utilityPaymentBill.setAmount(amount);
		utilityPaymentBill.setDueDate(dueDate);
		utilityPaymentBillService.saveAndRefresh(utilityPaymentBill)
			.orElseThrow(ServerErrorException::new);

		// return utility payment transaction
		final UtilityPaymentTransactionResponse result = new UtilityPaymentTransactionResponse(utilityPaymentTransaction, utilityPaymentBill);
		return ResponseEntity.status(HttpStatus.CREATED).body(result);
	}

	/**
	 * Inicializa el pago de una transacción mediante webpay.
	 *
	 * @param publicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/transactions/{id:[0-9a-f]{32}}/webpay")
	public ResponseEntity<UtilityPaymentWebpayResponse> webpay(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final UtilityPaymentTransactionPay utilityPaymentTransactionPay
	) {
		MDC.put("transaction", utils.mdc(publicId));

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
		return ResponseEntity.ok(new UtilityPaymentWebpayResponse(utilityPaymentWebpay));
	}

	/**
	 * Inicializa el pago de una transacción mediante transferencia.
	 *
	 * @param publicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/transactions/{id:[0-9a-f]{32}}/eft")
	public ResponseEntity<UtilityPaymentEftResponse> eft(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final UtilityPaymentTransactionPay utilityPaymentTransactionPay
	) {
		MDC.put("transaction", utils.mdc(publicId));

		// get utility payment transaction by id and status
		final String tefPublicId = utils.uuid();
		final String tefNotifyId = utils.uuid();
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
		utilityPaymentEft.setOrder(tefResponse.getMcOrderId());
		utilityPaymentEft.setUrl(tefResponse.getRedirectUrl());
		utilityPaymentEftService.save(utilityPaymentEft).orElseThrow(ServerErrorException::new);

		// update utility payment transaction status and email
		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(UtilityPaymentTransaction.EFT);
		utilityPaymentTransaction.setEmail(utilityPaymentTransactionPay.getEmail());
		utilityPaymentTransactionService.save(utilityPaymentTransaction).orElseThrow(ServerErrorException::new);

		// return redirect url
		return ResponseEntity.ok(new UtilityPaymentEftResponse(utilityPaymentEft));
	}

	// TODO update readme
	// TODO mdc clear
	// TODO save totaliser by user email
	// TODO delete cache task
	// TODO email receipt bill auth code
	// TODO 1 peso mode
	// TODO dejar host db parameter en properties
	// TODO backup database
	// TODO mover archivos de ambientes a carpetas, env
	// TODO bo totalizadores task correccion
	// TODO subir bo
}