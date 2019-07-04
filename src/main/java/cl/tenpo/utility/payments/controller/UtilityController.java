package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.entity.Bill;
import cl.tenpo.utility.payments.entity.Category;
import cl.tenpo.utility.payments.entity.Utility;
import cl.tenpo.utility.payments.object.UtilityBillItem;
import cl.tenpo.utility.payments.object.UtilityBillsRequest;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.UtilityService;
import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.util.UtilityClient;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UtilityController
{
	private final BillService billService;
	private final UtilityClient utilityClient;
	private final UtilityService utilityService;

	public UtilityController(
		final BillService billService,
		final UtilityClient utilityClient,
		final UtilityService utilityService
	) {
		this.billService = billService;
		this.utilityClient = utilityClient;
		this.utilityService = utilityService;
	}

	@GetMapping("/v1/utility-payments/categories")
	public List<Category> categories(@RequestHeader final HttpHeaders headers)
	{
		return utilityService.findAllCategories();
	}

	@GetMapping("/v1/utility-payments/categories/{id:\\d+}/utilities")
	public List<Utility> category(@PathVariable("id") final long categoryId)
	{
		final Category category = utilityService.findCategoryById(categoryId).orElseThrow(Http::NotFound);
		return utilityService.findAllUtilitiesByCategoryId(category.getId());
	}

	@Transactional
	@PostMapping(path = "/v1/utility-payments/utilities/{id:\\d+}/bills", consumes = MediaType.APPLICATION_JSON_VALUE)
	public List<Bill> bills(@PathVariable("id") final long utilityId, @RequestBody @Valid final UtilityBillsRequest request)
	{
		// get request parameters
		final Utility utility = utilityService.findUtilityById(utilityId).orElseThrow(Http::NotFound);
		final String utilityCode = utility.getCode();
		final String utilityCollector = utility.getCollectorId();
		final String utilityIdentifier = request.getIdentifier();

		// get bills and save
		final List<Bill> result = new ArrayList<>();
		for (final UtilityBillItem mcb : utilityClient.getBills(utilityCode, utilityCollector, utilityIdentifier)) {
			final Bill bill = new Bill();
			bill.setStatus(Bill.CREATED);
			bill.setUtilityId(utility.getId());
			bill.setIdentifier(utilityIdentifier);
			bill.setDueDate(mcb.getDueDate());
			bill.setDescription(mcb.getDesc());
			bill.setAmount(mcb.getAmount());
			bill.setQueryId(mcb.getDebtDataId());
			bill.setQueryOrder(mcb.getOrder());
			bill.setQueryTransactionId(mcb.getMcCode());
			billService.save(bill).orElseThrow(Http::ServerError);
			bill.setUtility(utility);
			result.add(bill);
		}
		return result;
	}

	@GetMapping("/v1/utility-payments/utilities")
	public String utilities()
	{
		return utilityClient.getUtilities().orElseThrow(Http::ServerError);
	}
}
