package cl.tenpo.utility.payments.utility;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.bill.Bill;
import cl.tenpo.utility.payments.bill.BillService;
import cl.tenpo.utility.payments.category.Category;
import cl.tenpo.utility.payments.payment.method.PaymentMethod;
import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.util.Utils;

/**
 * @author Carlos Izquierdo
 */
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

	@GetMapping("/v1/utility-payments/payment-methods")
	public List<PaymentMethod> paymentMethods()
	{
		return utilityService.getPaymentMethods();
	}

	@GetMapping("/v1/utility-payments/categories")
	public List<Category> categories()
	{
		return utilityService.findAllCategories();
	}

	@GetMapping("/v1/utility-payments/categories/{id:\\d+}/utilities")
	public List<Utility> categoryUtilities(@PathVariable("id") final long categoryId)
	{
		final Category category = utilityService.findCategoryById(categoryId).orElseThrow(Http::NotFound);
		return utilityService.findAllUtilitiesByCategoryId(category.getId());
	}

	@Transactional
	@RequestMapping(
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.POST,
		path = "/v1/utility-payments/utilities/{id:\\d+}/bills"
	)
	public List<Bill> utilityBills(
		@PathVariable("id") final long utilityId,
		@RequestBody @Valid final UtilityBillsRequest request
	){
		// get request parameters
		final Utility utility = utilityService.findUtilityById(utilityId).orElseThrow(Http::NotFound);
		final String utilityCode = utility.getCode();
		final String identifier = request.getIdentifier();
		final String collector = utility.getCollectorId();

		// get multicaja bills and save
		final List<Bill> result = new ArrayList<>();
		for (final UtilityBillItem mcb : utilityClient.getBills(utilityCode, identifier, collector)) {
			final Bill bill = new Bill();
			bill.setStatus(Bill.CREATED);
			bill.setPublicId(Utils.uuid());
			bill.setUtilityId(utility.getId());
			bill.setIdentifier(identifier);
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

	@GetMapping("/v1/utility-payments/utilities/multicaja")
	public String utilitiesMulticaja()
	{
		return utilityClient.getUtilitiesMulticaja().orElseThrow(Http::ServerError);
	}
}
