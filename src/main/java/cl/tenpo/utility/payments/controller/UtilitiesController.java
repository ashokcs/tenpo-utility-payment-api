package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.exception.NotFoundException;
import cl.tenpo.utility.payments.exception.ServerErrorException;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.object.dto.MCBill;
import cl.tenpo.utility.payments.object.dto.UtilityBillsRequest;
import cl.tenpo.utility.payments.object.vo.Category;
import cl.tenpo.utility.payments.object.vo.PaymentMethod;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.UtilityService;
import cl.tenpo.utility.payments.util.Utils;
import cl.tenpo.utility.payments.util.http.UtilityClient;

/**
 * @author Carlos Izquierdo
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UtilitiesController
{
	private final BillService billService;
	private final UtilityClient utilityClient;
	private final UtilityService utilityService;

	public UtilitiesController(
		final BillService billService,
		final UtilityClient utilityClient,
		final UtilityService utilityService
	) {
		this.billService = billService;
		this.utilityClient = utilityClient;
		this.utilityService = utilityService;
	}

	@GetMapping("/v1/categories")
	public List<Category> categories()
	{
		return utilityService.getCategories();
	}

	@GetMapping("/v1/payment-methods")
	public List<PaymentMethod> paymentMethods()
	{
		return utilityService.getPaymentMethods();
	}

	@GetMapping("/v1/utilities")
	public List<Utility> utilities()
	{
		return utilityService.findAll();
	}

	@RequestMapping(
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE,
		method = RequestMethod.POST,
		path = "/v1/utilities/{id:\\d+}/bills"
	)
	public List<Bill> utilityBills(
		@PathVariable("id") final long utilityId,
		@RequestBody @Valid final UtilityBillsRequest request
	){
		// get request parameters
		final Utility utility = utilityService.findById(utilityId).orElseThrow(NotFoundException::new);
		final String utilityCode = utility.getCode();
		final String identifier = request.getIdentifier();
		final String collector = utility.getCollectorId();

		// get multicaja bills and save
		final List<Bill> result = new ArrayList<>();
		for (final MCBill mcb : utilityClient.getBills(utilityCode, identifier, collector)) {
			final Bill bill = new Bill();
			bill.setStatus(Bill.PENDING);
			bill.setPublicId(Utils.uuid());
			bill.setUtilityId(utility.getId());
			bill.setIdentifier(identifier);
			bill.setDueDate(mcb.getDueDate());
			bill.setDescription(mcb.getDesc());
			bill.setAmount(mcb.getAmount());
			bill.setQueryId(mcb.getDebtDataId());
			bill.setQueryOrder(mcb.getOrder());
			bill.setQueryTransactionId(mcb.getMcCode());
			billService.save(bill).orElseThrow(ServerErrorException::new);
			bill.setUtility(utility);
			result.add(bill);
		}
		return result;
	}

	@GetMapping("/v1/utilities/multicaja")
	public String utilitiesMulticaja()
	{
		return utilityClient.getUtilitiesMulticaja().orElseThrow(ServerErrorException::new);
	}
}
