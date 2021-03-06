package cl.tenpo.utility.payments.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.newrelic.api.agent.NewRelic;

import cl.tenpo.utility.payments.jpa.entity.Category;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.repository.CategoryRepository;
import cl.tenpo.utility.payments.jpa.repository.UtilityRepository;

@Service
public class UtilityService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);

	private final CategoryRepository categoryRepository;
	private final UtilityRepository utilityRepository;

	public UtilityService(
		final CategoryRepository categoryRepository,
		final UtilityRepository utilityMulticajaRepository
	) {
		this.categoryRepository = categoryRepository;
		this.utilityRepository = utilityMulticajaRepository;
	}

	@Cacheable(value = "categories.withCounter", unless = "#result.size() == 0")
	public List<Category> findAllCategoriesWithCounter()
	{
		try {
			return categoryRepository.findAllByStatusOrderByOrderAsc(Category.ENABLED)
					.stream()
					.map(c -> c.setQuantity(utilityRepository.countByCategoryId(c.getId())))
					.map(c -> replaceCategoryName(c))
					.filter(c -> !c.getName().equals("Efectivo"))
					.collect(Collectors.toList());
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@Cacheable(value = "categories", unless = "#result.size() == 0")
	public List<Category> findAllCategories()
	{
		try {
			return categoryRepository.findAllByStatusOrderByOrderAsc(Category.ENABLED)
					.stream()
					.filter(c -> !c.getName().equals("Efectivo"))
					.collect(Collectors.toList());
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

//	public List<Utility> findAllUtilities()
//	{
//		try {
//			return utilityRepository.findAllByOrderByIdAsc().stream()
//				.filter(utility -> !filtered().contains(utility.getCode()))
//				.collect(Collectors.toList());
//		} catch (final Exception e) {
//			logger.error(e.getMessage(), e);
//		}
//		return new ArrayList<>();
//	}

	public List<Utility> findAllUtilitiesByCategoryId(final Long categoryId)
	{
		try {
			return utilityRepository.findAllByCategoryIdAndStatusOrderByIdAsc(categoryId, Utility.ENABLED).stream()
					.filter(utility -> !filtered().contains(utility.getCode()))
					.collect(Collectors.toList());
//			final List<UtilitiesResponse> utilities = new ArrayList<>();
//			utilityRepository.findAllByCategoryIdOrderByIdAsc(categoryId)
//			.stream()
//			.filter(utility -> !filtered().contains(utility.getCode()))
//			.map(u -> new UtilityItem(u))
//			.sorted((x, z) -> x.getLetter().compareTo(z.getLetter()))
//			.collect(Collectors.groupingBy(UtilityItem::getLetter, TreeMap::new, Collectors.toList()))
//			.forEach((k, u) -> {
//				final UtilitiesResponse t = new UtilitiesResponse();
//				t.setTitle(k);
//				t.setData(u);
//				utilities.add(t);
//			});
//			return utilities;
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public Optional<Utility> findUtilityById(final Long id)
	{
		try {
			return utilityRepository.findById(id);
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Category> findCategoryById(final Long id)
	{
		try {
			return categoryRepository.findById(id);
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private Set<String> filtered()
	{
		return Stream.of("CUPON_PAGO", "EMPRESA DE PRUEBAS", "FONASA", "RECAUDA_REDFACIL")
				.collect(Collectors.toSet());
	}

	private Category replaceCategoryName(final Category category)
	{
		if (category != null) {
			if ("Cosm??tica".equals(category.getName())) category.setName("Venta por cat??logo");
			if ("Financiera".equals(category.getName())) category.setName("Cr??ditos y tarjetas");
			if ("Telecomunicaciones".equals(category.getName())) category.setName("Telefon??a, Internet y TV");
		}
		return category;
	}
}
