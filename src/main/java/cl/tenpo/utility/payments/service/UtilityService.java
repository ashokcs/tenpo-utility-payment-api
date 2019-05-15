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

import cl.tenpo.utility.payments.jpa.entity.Category;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.repository.CategoryRepository;
import cl.tenpo.utility.payments.jpa.repository.UtilityRepository;
import cl.tenpo.utility.payments.object.vo.PaymentMethod;

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

	@Cacheable(value = "categories", unless = "#result.size() == 0")
	public List<Category> findAllCategories()
	{
		try {
			return categoryRepository.findAll().stream()
					.map(c -> c.setUtilities(utilityRepository.countByCategoryId(c.getId())))
					.filter(c -> !c.getName().equals("Efectivo"))
					.collect(Collectors.toList());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<Utility> findAllUtilities()
	{
		try {
			return utilityRepository.findAllByOrderByIdAsc().stream()
				.filter(utility -> !filtered().contains(utility.getCode()))
				.collect(Collectors.toList());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public List<Utility> findAllUtilitiesByCategoryId(final Long categoryId)
	{
		try {
			return utilityRepository.findAllByCategoryIdOrderByIdAsc(categoryId).stream()
				.filter(utility -> !filtered().contains(utility.getCode()))
				.collect(Collectors.toList());
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

	public List<PaymentMethod> getPaymentMethods()
	{
		final List<PaymentMethod> result = new ArrayList<>();
		result.add(new PaymentMethod(1L, Transaction.WEBPAY, "Webpay", false, true));
		result.add(new PaymentMethod(2L, Transaction.TRANSFERENCIA, "Transferencia", false, true));
		result.add(new PaymentMethod(3L, Transaction.PREPAID, "Prepago", true, false));
		return result;
	}

	public Optional<PaymentMethod> getPaymentMethodById(final Long paymentMethodId)
	{
		return getPaymentMethods().stream().filter(p -> p.getId().equals(paymentMethodId)).findFirst();
	}

	public Optional<PaymentMethod> getPaymentMethodByCode(final String paymentMethodCode)
	{
		return getPaymentMethods().stream().filter(p -> p.getCode().equals(paymentMethodCode)).findFirst();
	}

	private Set<String> filtered()
	{
		return Stream.of("CUPON_PAGO", "EMPRESA DE PRUEBAS", "FONASA", "RECAUDA_REDFACIL")
				.collect(Collectors.toSet());
	}
}
