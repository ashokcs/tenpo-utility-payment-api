package cl.multipay.utility.payments.controller;

import java.util.Optional;
import java.util.Random;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.BillPayment;
import cl.multipay.utility.payments.dto.MulticajaCreateOrderResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.Payment;
import cl.multipay.utility.payments.exception.ConfictException;
import cl.multipay.utility.payments.exception.InternalServerErrorException;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.MulticajaService;
import cl.multipay.utility.payments.service.PaymentService;
import cl.multipay.utility.payments.util.Utils;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class BillsController
{
	private static final Logger logger = LoggerFactory.getLogger(BillsController.class);

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

	@GetMapping("/v1/bills/{id:^[0-9a-f]{32}$}")
	public ResponseEntity<Bill> get(@PathVariable("id") final String billPublicId)
	{
		final Bill bill = billService.findByPublicId(billPublicId);
		if (bill != null) {
			return ResponseEntity.ok(bill);
		}
		throw new NotFoundException("Bill not found.");
	}

	@PostMapping("/v1/bills")
	public ResponseEntity<Bill> create(@RequestBody @Valid final BillPayment request)
	{
		// get utility bill
		// ... TODO
		final Long amount = (long) (new Random().nextInt((12000 - 1000) + 1) + 1000);

		// create bill
		final Bill bill = new Bill();
		bill.setPublicId(Utils.uuid());
		bill.setStatus(Bill.STATE_PENDING);
		bill.setUtilityId(request.getUtilityId());
		bill.setIdentifier(request.getIdentifier());
		bill.setAmount(amount);

		// save bill
		if (!billService.save(bill)) {
			throw new InternalServerErrorException("Error saving Bill.");
		}

		// generate response
		return ResponseEntity.ok().body(bill);
	}

	@PostMapping("/v1/bills/{id:^[0-9a-f]{32}$}/pay")
	public ResponseEntity<Payment> pay(@PathVariable("id") final String billPublicId)
	{
		// find bill
		final Bill bill = billService.findByPublicId(billPublicId);
		if (bill != null) {

			// check bill state
			if (!Bill.STATE_PENDING.equals(bill.getStatus())) {
				throw new ConfictException("Bill invalid state.");
			}

			// call multicaja
			final Optional<MulticajaCreateOrderResponse> multicajaPaymentOptional = multicajaService.initPay(bill);
			if (!multicajaPaymentOptional.isPresent()) {
				throw new InternalServerErrorException("Error creating payment.");
			}

			// save payment
			final MulticajaCreateOrderResponse multicajaPayment = multicajaPaymentOptional.get();
			final Payment payment = new Payment();
			payment.setBillId(bill.getId());
			payment.setPublicId(Utils.uuid());
			payment.setStatus(Payment.STATE_PENDING);
			payment.setOrderId(multicajaPayment.getOrderId());
			payment.setRedirectUrl(multicajaPayment.getRedirectUrl());
			if (!paymentService.save(payment)) {
				throw new InternalServerErrorException("Error saving Payment.");
			}

			// update bill state // TODO fix no update state
			bill.setStatus(Bill.STATE_WAITING);
			if (!billService.save(bill)) {
				throw new InternalServerErrorException("Error updating Bill.");
			}

			// return url
			return ResponseEntity.ok(payment);
		}
		throw new NotFoundException("Bill not found.");
	}
}