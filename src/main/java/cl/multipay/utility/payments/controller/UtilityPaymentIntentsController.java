package cl.multipay.utility.payments.controller;

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

import cl.multipay.utility.payments.dto.UtilityPaymentIntentRequest;
import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentIntent;
import cl.multipay.utility.payments.service.UtilityPaymentIntentService;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilityPaymentIntentsController
{
	private final UtilityPaymentIntentService utilityPaymentIntentService;

	public UtilityPaymentIntentsController(final UtilityPaymentIntentService upis)
	{
		this.utilityPaymentIntentService = upis;
	}

	@GetMapping("/v1/utilities/payment_intents/{id}")
	public ResponseEntity<UtilityPaymentIntent> get(@PathVariable("id") final String utilityPaymentIntentId)
	{
		final UtilityPaymentIntent utilityPaymentIntent = utilityPaymentIntentService.findByUuid(utilityPaymentIntentId);
		if (utilityPaymentIntent != null) {
			return ResponseEntity.ok(utilityPaymentIntent);
		}
		return ResponseEntity.notFound().build();
	}

	@PostMapping("/v1/utilities/payment_intents")
	public ResponseEntity<?> get(@RequestBody @Valid final UtilityPaymentIntentRequest request)
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
}