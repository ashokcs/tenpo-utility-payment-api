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

import cl.multipay.utility.payments.dto.BillRequest;
import cl.multipay.utility.payments.dto.MulticajaBill;
import cl.multipay.utility.payments.dto.MulticajaInitPayResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.Payment;
import cl.multipay.utility.payments.exception.ConfictException;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.MulticajaService;
import cl.multipay.utility.payments.service.PaymentService;
import cl.multipay.utility.payments.util.Utils;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class BillsController
{
	private final BillService billService;
	private final PaymentService paymentService;
	private final MulticajaService multicajaService;

	public BillsController(final BillService billService,
		final PaymentService paymentService, final MulticajaService multicajaService)
	{
		this.billService = billService;
		this.paymentService = paymentService;
		this.multicajaService = multicajaService;
	}

	// TODO database creation automation script

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
		final MulticajaBill billDetails = multicajaService.getBill(utility, collector).orElseThrow(ConfictException::new);

		// get response
		final Long amount = billDetails.getAmount();
		final String transactionId = billDetails.getTransactionId();
		final String dueDate = billDetails.getDueDate();

		// create bill
		final Bill bill = new Bill();
		bill.setPublicId(Utils.uuid());
		bill.setStatus(Bill.STATUS_PENDING);
		bill.setUtility(utility);
		bill.setCollector(collector);
		bill.setIdentifier(identifier);
		bill.setAmount(amount);
		bill.setDueDate(dueDate);
		bill.setTransactionId(transactionId);
		billService.save(bill).orElseThrow(ServerErrorException::new);

		// return bill
		return ResponseEntity.status(HttpStatus.CREATED).body(bill);
	}

	/**
	 * Inicializa el pago de una cuenta.
	 *
	 * @param billPublicId Identificador público de la cuenta
	 * @return Url de redirección para continuar el pago
	 */
	@PostMapping("/v1/bills/{id:^[0-9a-f]{32}$}/pay")
	public ResponseEntity<Payment> pay(@PathVariable("id") final String billPublicId)
	{
		// get bill by id and status
		final Bill bill = billService.findByPublicId(billPublicId, Bill.STATUS_PENDING)
				.orElseThrow(NotFoundException::new);

		// initialize remote payment
		final MulticajaInitPayResponse multicajaInitPayResponse = multicajaService.initPay(bill)
				.orElseThrow(ServerErrorException::new);

		// save payment response
		final Payment payment = new Payment();
		payment.setBillId(bill.getId());
		payment.setPublicId(Utils.uuid());
		payment.setStatus(Payment.STATUS_PENDING);
		payment.setOrderId(multicajaInitPayResponse.getOrderId());
		payment.setRedirectUrl(multicajaInitPayResponse.getRedirectUrl());
		paymentService.save(payment).orElseThrow(ServerErrorException::new);

		// update bill status // TODO fix no update state
		bill.setStatus(Bill.STATUS_WAITING);
		billService.save(bill).orElseThrow(ServerErrorException::new);

		// return redirect url
		return ResponseEntity.ok(payment);
	}
}