package cl.tenpo.utility.payments.controller;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Category;
import cl.tenpo.utility.payments.jpa.entity.Favorite;
import cl.tenpo.utility.payments.jpa.entity.Option;
import cl.tenpo.utility.payments.jpa.entity.Suggestion;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.entity.UtilityTimeout;
import cl.tenpo.utility.payments.jpa.entity.Welcome;
import cl.tenpo.utility.payments.jpa.repository.BillRepository;
import cl.tenpo.utility.payments.jpa.repository.FavoriteRepository;
import cl.tenpo.utility.payments.jpa.repository.OptionRepository;
import cl.tenpo.utility.payments.jpa.repository.SuggestionRepository;
import cl.tenpo.utility.payments.jpa.repository.UtilityRepository;
import cl.tenpo.utility.payments.jpa.repository.UtilityTimeoutRepository;
import cl.tenpo.utility.payments.jpa.repository.WelcomeRepository;
import cl.tenpo.utility.payments.object.HomeResponse;
import cl.tenpo.utility.payments.object.UtilityBillItem;
import cl.tenpo.utility.payments.object.UtilityBillResponse;
import cl.tenpo.utility.payments.object.UtilityBillsRequest;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.UtilityService;
import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.util.Properties;
import cl.tenpo.utility.payments.util.UtilityClient;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UtilityController
{
	private final BillService billService;
	private final BillRepository billRepository;
	private final UtilityClient utilityClient;
	private final UtilityService utilityService;
	private final SuggestionRepository suggestionRepository;
	private final UtilityRepository utilityRepository;
	private final FavoriteRepository favoriteRepository;
	private final OptionRepository optionRepository;
	private final Properties properties;
	private final UtilityTimeoutRepository utilityTimeoutRepository;
	private final WelcomeRepository welcomeRepository;

	public UtilityController(
		final BillService billService,
		final BillRepository billRepository,
		final UtilityClient utilityClient,
		final UtilityService utilityService,
		final FavoriteRepository favoriteRepository,
		final OptionRepository optionRepository,
		final SuggestionRepository suggestionRepository,
		final UtilityRepository utilityRepository,
		final Properties properties,
		final UtilityTimeoutRepository utilityTimeoutRepository,
		final WelcomeRepository welcomeRepository
	) {
		this.billService = billService;
		this.billRepository = billRepository;
		this.utilityClient = utilityClient;
		this.utilityService = utilityService;
		this.favoriteRepository = favoriteRepository;
		this.optionRepository = optionRepository;
		this.suggestionRepository = suggestionRepository;
		this.utilityRepository = utilityRepository;
		this.properties = properties;
		this.utilityTimeoutRepository = utilityTimeoutRepository;
		this.welcomeRepository = welcomeRepository;
	}

	@GetMapping("/v1/utility-payments/categories")
	public List<Category> categories()
	{
		return utilityService.findAllCategoriesWithCounter();
	}

	@GetMapping("/v1/utility-payments/categories/{id:\\d+}/utilities")
	public List<Utility> category(@PathVariable("id") final long categoryId)
	{
		final Category category = utilityService.findCategoryById(categoryId).orElseThrow(Http::NotFound);
		return utilityService.findAllUtilitiesByCategoryId(category.getId());
	}

	@Transactional
	@PostMapping(path = "/v1/utility-payments/utilities/{id:\\d+}/bills", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Bill>> bills(
		@RequestHeader(value="x-mine-user-id") final UUID userId,
		@PathVariable("id") final long utilityId,
		@RequestBody @Valid final UtilityBillsRequest request
	){
		final List<Bill> result = new ArrayList<>();

		// get request parameters
		final Utility utility = utilityService.findUtilityById(utilityId).orElseThrow(Http::NotFound);
		final Category category = utilityService.findCategoryById(utility.getCategoryId()).orElseThrow(Http::NotFound);
		utility.setCategory(category);
		final String utilityCode = utility.getCode();
		final String utilityCollector = utility.getCollectorId();
		final String utilityIdentifier = request.getIdentifier();

		// check if timeout
		final Optional<UtilityTimeout> utilityTimeout = utilityTimeoutRepository.findByUtilityId(utility.getId());

		// check if recently paid
		final Integer timeout = utilityTimeout.isPresent() ? utilityTimeout.get().getTimeout() : properties.billsRecentlyPaidMinusMinutes;
		final OffsetDateTime created = OffsetDateTime.now().minusMinutes(timeout);
		final Optional<Bill> opt = billRepository.findFirstByIdentifierAndUtilityIdAndUserAndStatusAndCreatedGreaterThanOrderByCreatedDesc(
				utilityIdentifier, utility.getId(), userId, Bill.SUCCEEDED, created);
		if (opt.isPresent()) return ResponseEntity.ok(result);

		// get bills
		final UtilityBillResponse response = utilityClient.getBills(utilityCode, utilityCollector, utilityIdentifier);
		final List<UtilityBillItem> bills = response.getBills();

		// check if service is unavailable
		if (response.isUnavailable()) {
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(result);
		}

		// check if same amount
		if (!bills.isEmpty() && bills.size() == 2) {
			if (bills.get(0).getAmount().equals(bills.get(1).getAmount())) {
				bills.remove(1);
			}
		}

		// save bills
		for (final UtilityBillItem bill : bills) {
			final Bill tmp = new Bill();
			tmp.setStatus(Bill.CREATED);
			tmp.setUser(userId);
			tmp.setUtilityId(utility.getId());
			tmp.setIdentifier(utilityIdentifier);
			tmp.setDueDate(bill.getDueDate());
			tmp.setDescription(bill.getDesc());
			tmp.setAmount(bill.getAmount());
			tmp.setQueryId(bill.getDebtDataId());
			tmp.setQueryOrder(bill.getOrder());
			tmp.setQueryTransactionId(bill.getMcCode());
			billService.save(tmp).orElseThrow(Http::ServerError);
			tmp.setUtility(utility);
			result.add(tmp);
		}
		return ResponseEntity.ok(result);
	}

	@GetMapping("/v1/utility-payments/bills/{id}")
	public Bill bill(
		@RequestHeader(value="x-mine-user-id") final UUID userId,
		@PathVariable("id") final UUID billId
	){
		final Optional<Bill> optional = billService.findById(billId, userId);
		if (optional.isPresent()) {
			return billService.findById(billId, userId).get();
		}
		throw Http.NotFound();
	}

	@GetMapping("/v1/utility-payments/utilities")
	public String utilities()
	{
		return utilityClient.getUtilities().orElseThrow(Http::ServerError);
	}

	@GetMapping("/v1/utility-payments/home")
	public HomeResponse home(@RequestHeader("x-mine-user-id") final UUID user)
	{
		// get categories and generate map
		final Map<Long, Category> categories = utilityService
				.findAllCategories().stream()
				.collect(Collectors.toMap(c -> c.getId(), c -> c));

		// get favorites and add category
		final List<Favorite> favorites = favoriteRepository.findFirst20ByUserOrderById(user).stream().map(f -> {
			f.getUtility().setCategory(categories.get(f.getUtility().getCategoryId()));
			return f;
		}).collect(Collectors.toList());

		// get suggestions
		final List<Suggestion> suggestions = suggestionRepository.findFirst20ByUserAndStatusAndExpiredGreaterThanOrderByCreatedAsc(user, Suggestion.ENABLED, OffsetDateTime.now());
		suggestions.forEach(s -> {s.setUtility(utilityRepository.findById(s.getUtilityId()).get());});
		suggestions.stream().map(s -> {
			s.getUtility().setCategory(categories.get(s.getUtility().getCategoryId()));
			return s;
		}).collect(Collectors.toList());

		// get user options
		final Optional<Option> options = optionRepository.findByUser(user);
		if (!options.isPresent() || (options.isPresent() && options.get().isSuggest() == false)) suggestions.clear();

		// get tos/welcome
		final Optional<Welcome> opt = welcomeRepository.findByUser(user);
		final Welcome welcome;
		if (opt.isPresent()) {
			welcome = opt.get();
			if (welcome.getTos().equals(1)) welcome.setVisits(welcome.getVisits()+1);
			welcomeRepository.save(welcome);
		} else {
			welcome = new Welcome();
			welcome.setUser(user);
			welcome.setCreated(OffsetDateTime.now());
			welcome.setVisits(0);
			welcome.setTos(0);
			welcomeRepository.save(welcome);
		}

		return new HomeResponse(favorites, suggestions, welcome);
	}
}
