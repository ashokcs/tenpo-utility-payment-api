package cl.multipay.utility.payments.controller;

import java.util.Random;

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
import cl.multipay.utility.payments.dto.MulticajaInitPayResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.Payment;
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
		// get utility bill
		multicajaService.getBill(request.getUtility(), request.getCollector());
		final Long amount = (long) (new Random().nextInt((12000 - 1000) + 1) + 1000);

		// create and save bill
		final Bill bill = new Bill();
		bill.setPublicId(Utils.uuid());
		bill.setStatus(Bill.STATUS_PENDING);
		bill.setUtility(request.getUtility());
		bill.setIdentifier(request.getIdentifier());
		bill.setAmount(amount);
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