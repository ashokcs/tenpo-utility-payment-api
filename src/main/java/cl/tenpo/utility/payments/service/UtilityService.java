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

import cl.tenpo.utility.payments.entity.Category;
import cl.tenpo.utility.payments.entity.Utility;
import cl.tenpo.utility.payments.repository.CategoryRepository;
import cl.tenpo.utility.payments.repository.UtilityRepository;

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
			return categoryRepository.findAllByStatusOrderByNameAsc(Category.ENABLED)
					.stream()
					.map(c -> c.setQuantity(utilityRepository.countByCategoryId(c.getId())))
					.filter(c -> !c.getName().equals("Efectivo"))
					.collect(Collectors.toList());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	@Cacheable(value = "categories", unless = "#result.size() == 0")
	public List<Category> findAllCategories()
	{
		try {
			return categoryRepository.findAllByStatusOrderByNameAsc(Category.ENABLED)
					.stream()
					.filter(c -> !c.getName().equals("Efectivo"))
					.collect(Collectors.toList());
		} catch (final Exception e) {
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
			return utilityRepository.findAllByCategoryIdOrderByIdAsc(categoryId).stream()
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
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public Optional<Utility> findUtilityById(final Long id)
	{
		try {
			return utilityRepository.findById(id);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Category> findCategoryById(final Long id)
	{
		try {
			return categoryRepository.findById(id);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private Set<String> filtered()
	{
		return Stream.of("CUPON_PAGO", "EMPRESA DE PRUEBAS", "FONASA", "RECAUDA_REDFACIL")
				.collect(Collectors.toSet());
	}
}
