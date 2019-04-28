package cl.tenpo.utility.payments.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.repository.UtilityRepository;
import cl.tenpo.utility.payments.object.vo.Category;
import cl.tenpo.utility.payments.object.vo.PaymentMethod;

@Service
public class UtilityService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityService.class);

	private final UtilityRepository utilityRepository;

	public UtilityService(
		final UtilityRepository utilityMulticajaRepository
	) {
		this.utilityRepository = utilityMulticajaRepository;
	}

	public List<Utility> findAll()
	{
		try {
			final Set<String> filter = Stream.of("CUPON_PAGO", "EMPRESA DE PRUEBAS", "FONASA", "RECAUDA_REDFACIL")
					.collect(Collectors.toSet());
			return utilityRepository.findAllByOrderByIdAsc().stream()
				.filter(utility -> !filter.contains(utility.getCode()))
				.collect(Collectors.toList());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

	public Optional<Utility> findById(final Long id)
	{
		try {
			return utilityRepository.findById(id);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public List<Category> getCategories()
	{
		final List<Category> result = new ArrayList<>();
		result.add(new Category("100",  "AGUA"));
		result.add(new Category("500",  "AUTOPISTAS"));
		result.add(new Category("1100", "CEMENTERIO"));
		result.add(new Category("600",  "COSMETICA"));
		result.add(new Category("800",  "CREDITO-FINANCIERA"));
		result.add(new Category("1000", "EDUCACION"));
		//result.add(new Category("1300", "EFECTIVO MULTICAJA"));
		result.add(new Category("400",  "GAS"));
		result.add(new Category("200",  "LUZ"));
		result.add(new Category("1200", "OTRAS EMPRESAS"));
		result.add(new Category("700",  "RETAIL"));
		result.add(new Category("900",  "SEGURIDAD"));
		result.add(new Category("300",  "TELEF-TV-INTERNET"));
		return result;
	}

	public List<PaymentMethod> getPaymentMethods()
	{
		final List<PaymentMethod> result = new ArrayList<>();
		result.add(new PaymentMethod("webpay", "Webpay", false, true));
		result.add(new PaymentMethod("transferencia", "Transferencia", false, true));
		result.add(new PaymentMethod("prepaid", "Prepago", true, false));
		return result;
	}
}
