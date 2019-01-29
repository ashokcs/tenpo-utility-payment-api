package cl.multipay.utility.payments.controller;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.MulticajaCreateOrderResponse;
import cl.multipay.utility.payments.dto.UtilityPaymentIntentRequest;
import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentIntent;
import cl.multipay.utility.payments.exception.ConfictException;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.service.MulticajaPaymentPlatformService;
import cl.multipay.utility.payments.service.UtilityPaymentIntentService;
import cl.multipay.utility.payments.util.Utils;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilityPaymentIntentsController
{
	private final UtilityPaymentIntentService utilityPaymentIntentService;
	private final MulticajaPaymentPlatformService multicajaPaymentPlatformService;

	public UtilityPaymentIntentsController(final UtilityPaymentIntentService upis, final MulticajaPaymentPlatformService pps)
	{
		utilityPaymentIntentService = upis;
		multicajaPaymentPlatformService = pps;
	}

	@GetMapping("/v1/utilities/payment_intents/{id:"+Utils.UUID_REGEX+"}")
	public ResponseEntity<UtilityPaymentIntent> get(@PathVariable("id") final String utilityPaymentIntentId)
	{
		final UtilityPaymentIntent utilityPaymentIntent = utilityPaymentIntentService.findByUuid(utilityPaymentIntentId);
		if (utilityPaymentIntent != null) {
			return ResponseEntity.ok(utilityPaymentIntent);
		}
		throw new NotFoundException("Payment Intent not found.");
	}

	@PostMapping("/v1/utilities/payment_intents")
	public ResponseEntity<UtilityPaymentIntent> create(@RequestBody @Valid final UtilityPaymentIntentRequest request)
	{
		// create utility payment intent
		final UtilityPaymentBill utilityPaymentBill = new UtilityPaymentBill();
		utilityPaymentBill.setState(UtilityPaymentBill.STATE_PENDING);
		utilityPaymentBill.setUtility(request.getUtility());
		utilityPaymentBill.setIdentifier(request.getIdentifier());

		final UtilityPaymentIntent utilityPaymentIntent = new UtilityPaymentIntent();
		utilityPaymentIntent.setUuid(UUID.randomUUID().toString());
		utilityPaymentIntent.setState(UtilityPaymentIntent.STATE_PENDING);
		utilityPaymentIntent.setEmail(request.getEmail());
		utilityPaymentIntent.getBills().add(utilityPaymentBill);

		// save utility payment
		utilityPaymentIntentService.save(utilityPaymentIntent);

		// get utility bill
		// ...

		// save utility bill
		// ...

		// generate response
		return ResponseEntity.ok().body(utilityPaymentIntent);
	}

	@PostMapping("/v1/utilities/payment_intents/{id:"+Utils.UUID_REGEX+"}/webpay")
	public ResponseEntity<?> pay(@PathVariable("id") final String utilityPaymentIntentId)
	{
		final UtilityPaymentIntent utilityPaymentIntent = utilityPaymentIntentService.findByUuid(utilityPaymentIntentId);
		if (utilityPaymentIntent != null) {

			// check payment intent state is pending
			if (!UtilityPaymentIntent.STATE_PENDING.equals(utilityPaymentIntent.getState())) {
				throw new ConfictException("Payment Intent invalid state.");
			}

			// call multicaja payment platform
			final Optional<MulticajaCreateOrderResponse> payment = multicajaPaymentPlatformService.initPay(utilityPaymentIntent);
			if (!payment.isPresent()) {
				throw new ConfictException("Error creating payment.");
			}

			// save webpay
			// update state
			// return url
			return ResponseEntity.ok(utilityPaymentIntent);
		}
		throw new NotFoundException("Payment Intent not found.");
	}
}