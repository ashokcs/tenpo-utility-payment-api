package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.dto.BillsRequest;
import cl.tenpo.utility.payments.dto.MulticajaBill;
import cl.tenpo.utility.payments.exception.ServerErrorException;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.util.Utils;
import cl.tenpo.utility.payments.util.http.UtilitiesClient;

/**
 * @author Carlos Izquierdo
 */
@RestController
@RequestMapping(
	consumes = MediaType.APPLICATION_JSON_VALUE,
	produces = MediaType.APPLICATION_JSON_VALUE
)
public class BillsController
{
	private final BillService billService;
	private final UtilitiesClient utilityPaymentClient;

	public BillsController(
			final BillService billService,
		final UtilitiesClient utilityPaymentClient
	){
		this.billService = billService;
		this.utilityPaymentClient = utilityPaymentClient;
	}

	@Transactional
	@PostMapping("/v1/bills")
	public List<Bill> bills(
		@RequestBody @Valid final BillsRequest request
	){
		// get request parameters
		final String utility = request.getUtilityCode();
		final String collector = request.getCollectorId();
		final String category = request.getCategoryId();
		final String identifier = request.getIdentifier();

		// get multicaja bills
		final List<MulticajaBill> bills = utilityPaymentClient.getBills(utility, identifier, collector);
		final List<Bill> result = new ArrayList<>();

		// save bills
		for (final MulticajaBill b : bills) {
			final Bill bill = new Bill();
			bill.setStatus(Bill.WAITING);
			bill.setPublicId(Utils.uuid());
			bill.setUtility(utility);
			bill.setCollectorId(collector);
			bill.setCollectorName(Utils.collectorFiendlyName(collector));
			bill.setCategoryId(category);
			bill.setCategoryName(Utils.categoryFriendlyName(category));
			bill.setIdentifier(identifier);
			bill.setDueDate(b.getDueDate());
			bill.setDescription(b.getDesc());
			bill.setAmount(b.getAmount());
			bill.setQueryId(b.getDebtDataId());
			bill.setQueryOrder(b.getOrder());
			bill.setQueryTransactionId(b.getMcCode());
			billService.save(bill).orElseThrow(ServerErrorException::new);
			result.add(bill);
		}
		return result;
	}
}
