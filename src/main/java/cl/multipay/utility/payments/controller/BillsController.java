package cl.multipay.utility.payments.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.GetBillsRequest;
import cl.multipay.utility.payments.dto.MulticajaBill;
import cl.multipay.utility.payments.http.UtilityPaymentClient;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class BillsController
{
	private final UtilityPaymentClient utilityPaymentClient;

	public BillsController(final UtilityPaymentClient utilityPaymentClient)
	{
		this.utilityPaymentClient = utilityPaymentClient;
	}

	@PostMapping("/v2/bills")
	public ResponseEntity<List<MulticajaBill>> getBills(
		@RequestBody @Valid final GetBillsRequest request)
	{
		final String utility = request.getUtility();
		final String collector = request.getCollector();
		final String category = request.getCategory();
		final String identifier = request.getIdentifier();

		// get bills
		final List<MulticajaBill> bills = utilityPaymentClient.getBills(utility, identifier, collector);

		// save bills
		for (final MulticajaBill mcb : bills) {

		}

		return ResponseEntity.ok(bills);
	}
}
