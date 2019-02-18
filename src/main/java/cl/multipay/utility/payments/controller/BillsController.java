package cl.multipay.utility.payments.controller;

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

import cl.multipay.utility.payments.dto.BillPayRequest;
import cl.multipay.utility.payments.dto.BillRequest;
import cl.multipay.utility.payments.dto.MulticajaBill;
import cl.multipay.utility.payments.dto.TefCreateOrderResponse;
import cl.multipay.utility.payments.dto.WebpayInitResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.TransferenciaPayment;
import cl.multipay.utility.payments.entity.WebpayPayment;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.http.TransferenciaClient;
import cl.multipay.utility.payments.http.UtilityPaymentClient;
import cl.multipay.utility.payments.http.WebpayClient;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.TransferenciaPaymentService;
import cl.multipay.utility.payments.service.WebpayPaymentService;
import cl.multipay.utility.payments.util.Utils;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class BillsController
{
	private final BillService billService;
	private final WebpayPaymentService webpayPaymentService;
	private final TransferenciaPaymentService transferenciaPaymentService;

	private final WebpayClient webpayClient;
	private final TransferenciaClient transferenciaClient;
	private final UtilityPaymentClient utilityPaymentClient;

	public BillsController(final BillService billService,
		final WebpayPaymentService webpayPaymentService, final UtilityPaymentClient utilityPaymentClient,
		final WebpayClient webpayClient, final TransferenciaClient transferenciaClient,
		final TransferenciaPaymentService transferenciaPaymentService)
	{
		this.billService = billService;
		this.webpayPaymentService = webpayPaymentService;
		this.utilityPaymentClient = utilityPaymentClient;
		this.webpayClient = webpayClient;
		this.transferenciaClient = transferenciaClient;
		this.transferenciaPaymentService = transferenciaPaymentService;
	}

	/**
	 * Obtiene los detalles de una cuenta.
	 *
	 * @param billPublicId Identificador público de la cuenta
	 * @return Los datos de la cuenta
	 */
	@GetMapping("/v1/bills/{id:^[0-9a-f]{32}$}")
	public ResponseEntity<Bill> get(@PathVariable("id") final String billPublicId)
	{
		final Bill bill = billService.findByPublicId(billPublicId).orElseThrow(NotFoundException::new);
		return ResponseEntity.ok(bill);
	}

	/**
	 *	Consulta y crea la deuda de una cuenta.
	 *
	 * @param request El identificador del servicio y cuenta a consultar
	 * @return Los datos de la cuenta
	 */
	@PostMapping("/v1/bills")
	public ResponseEntity<Bill> create(@RequestBody @Valid final BillRequest request)
	{
		// TODO validate identifier

		// get utility bill
		final String utility = request.getUtility();
		final String collector = request.getCollector();
		final String identifier = request.getIdentifier();
		final MulticajaBill billDetails = utilityPaymentClient.getBill(utility, collector)
				.orElseThrow(ServerErrorException::new);

		// get response
		final Long amount = billDetails.getAmount();
		final String transactionId = billDetails.getTransactionId();
		final String dueDate = billDetails.getDueDate();

		// create bill
		final Bill bill = new Bill();
		bill.setPublicId(Utils.uuid());
		bill.setStatus(Bill.PENDING);
		bill.setUtility(utility);
		bill.setCollector(collector);
		bill.setIdentifier(identifier);
		bill.setAmount(amount);
		bill.setDueDate(dueDate);
		bill.setTransactionId(transactionId);
		billService.saveAndRefresh(bill).orElseThrow(ServerErrorException::new);

		// return bill
		return ResponseEntity.status(HttpStatus.CREATED).body(bill);
	}

	/**
	 * Inicializa el pago de una cuenta mediante webpay.
	 *
	 * @param billPublicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/bills/{id:^[0-9a-f]{32}$}/webpay")
	public ResponseEntity<WebpayPayment> payWebpay(
		@PathVariable("id") final String billPublicId,
		@RequestBody @Valid final BillPayRequest billPayRequest
	) {
		// get bill by id and status
		final Bill bill = billService.getPendingByPublicId(billPublicId).orElseThrow(NotFoundException::new);

		// webpay init payment
		final WebpayInitResponse webpayResponse = webpayClient.init(bill)
				.orElseThrow(ServerErrorException::new);

		// save payment response
		final WebpayPayment webpay = new WebpayPayment();
		webpay.setBillId(bill.getId());
		webpay.setStatus(WebpayPayment.PENDING);
		webpay.setToken(webpayResponse.getToken());
		webpay.setUrl(webpayResponse.getUrl());
		webpayPaymentService.save(webpay).orElseThrow(ServerErrorException::new);

		// update bill status and email
		bill.setStatus(Bill.WAITING);
		bill.setPayment(Bill.WEBPAY);
		bill.setEmail(billPayRequest.getEmail());
		billService.save(bill).orElseThrow(ServerErrorException::new);

		// return redirect url
		return ResponseEntity.ok(webpay);
	}

	/**
	 * Inicializa el pago de una cuenta mediante transferencia.
	 *
	 * @param billPublicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/bills/{id:^[0-9a-f]{32}$}/transferencia")
	public ResponseEntity<TransferenciaPayment> payTef(
		@PathVariable("id") final String billPublicId,
		@RequestBody @Valid final BillPayRequest billPayRequest
	) {
		// get bill by id and status
		final String tefPublicId = Utils.uuid();
		final String tefNotifyId = Utils.uuid();
		final Bill bill = billService.getPendingByPublicId(billPublicId).orElseThrow(NotFoundException::new);

		// transferencia create order
		final TefCreateOrderResponse tefResponse = transferenciaClient.createOrder(bill, tefPublicId, tefNotifyId)
				.orElseThrow(ServerErrorException::new);

		// save payment response
		final TransferenciaPayment transferenciaPayment = new TransferenciaPayment();
		transferenciaPayment.setBillId(bill.getId());
		transferenciaPayment.setStatus(TransferenciaPayment.PENDING);
		transferenciaPayment.setPublicId(tefPublicId);
		transferenciaPayment.setNotifyId(tefNotifyId);
		transferenciaPayment.setMcOrderId(tefResponse.getMcOrderId());
		transferenciaPayment.setUrl(tefResponse.getRedirectUrl());
		transferenciaPaymentService.save(transferenciaPayment).orElseThrow(ServerErrorException::new);

		// update bill status and email
		bill.setStatus(Bill.WAITING);
		bill.setPayment(Bill.TRANSFERENCIA);
		bill.setEmail(billPayRequest.getEmail());
		billService.save(bill).orElseThrow(ServerErrorException::new);

		// return redirect url
		return ResponseEntity.ok(transferenciaPayment);
	}
	// TODO Subir azure
	// TODO html correo comprobante
	// TODO migrations
	// TODO dejar host db parameter en properties
	// TODO docker file staging production env
	// TODO rename properties redirect to front
	// TODO backup database
	// TODO mover archivos de ambientes a carpetas, env
	// TODO bo totalizadores
	// TODO bo totalizadores task correccion
	// TODO check time on azure db, gmt?
}