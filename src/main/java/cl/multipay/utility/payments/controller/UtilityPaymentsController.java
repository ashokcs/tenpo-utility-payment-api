package cl.multipay.utility.payments.controller;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.UtilityPaymentRequest;
import cl.multipay.utility.payments.entity.UtilityPayment;
import cl.multipay.utility.payments.repository.UtilityPaymentRepository;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilityPaymentsController
{
	private final UtilityPaymentRepository utilityPaymentRepository;

	public UtilityPaymentsController(final UtilityPaymentRepository utilityPaymentRepository)
	{
		this.utilityPaymentRepository = utilityPaymentRepository;
	}

	@PostMapping("/v1/utilities")
	public ResponseEntity<?> get(@RequestBody @Valid final UtilityPaymentRequest request)
	{
		// get parameters
		// ...

		// create utility
		UtilityPayment utilityPayment = new UtilityPayment();
		utilityPayment.setUuid(UUID.randomUUID().toString());
		utilityPayment.setState(UtilityPayment.STATE_PENDING);
		utilityPaymentRepository.saveAndFlush(utilityPayment);
		utilityPayment = utilityPaymentRepository.getOne(utilityPayment.getId());

		// get utility deb
		// ...

		// update utility
		// ...

		// generate response
		return ResponseEntity.ok().body(utilityPayment);
	}
}