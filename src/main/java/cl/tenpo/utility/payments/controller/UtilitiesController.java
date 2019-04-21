package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.dto.Category;
import cl.tenpo.utility.payments.dto.PaymentMethod;
import cl.tenpo.utility.payments.dto.Utility;
import cl.tenpo.utility.payments.exception.ServerErrorException;
import cl.tenpo.utility.payments.util.http.UtilitiesClient;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class UtilitiesController
{
	private final UtilitiesClient utilityPaymentClient;

	public UtilitiesController(final UtilitiesClient multicajaService)
	{
		this.utilityPaymentClient = multicajaService;
	}

	@GetMapping("/v1/categories")
	@Cacheable(value = "categories", unless = "#result.size() == 0")
	public List<Category> categories()
	{
		final List<Category> result = new ArrayList<>();
		result.add(new Category("100", "AGUA"));
		result.add(new Category("200", "LUZ"));
		result.add(new Category("300", "TELEF-TV-INTERNET"));
		result.add(new Category("400", "GAS"));
		result.add(new Category("500", "AUTOPISTAS"));
		result.add(new Category("600", "COSMETICA"));
		result.add(new Category("700", "RETAIL"));
		result.add(new Category("800", "CREDITO-FINANCIERA"));
		result.add(new Category("900", "SEGURIDAD"));
		result.add(new Category("1000", "EDUCACION"));
		result.add(new Category("1100", "CEMENTERIO"));
		result.add(new Category("1200", "OTRAS EMPRESAS"));
		result.add(new Category("1300", "EFECTIVO MULTICAJA"));
		return result;
	}

	@GetMapping("/v1/payment-methods")
	@Cacheable(value = "payment-methods", unless = "#result.size() == 0")
	public List<PaymentMethod> paymentMethods()
	{
		final List<PaymentMethod> result = new ArrayList<>();
		result.add(new PaymentMethod("webpay", "Webpay"));
		result.add(new PaymentMethod("transferencia", "Transferencia"));
		return result;
	}

	@GetMapping("/v1/utilities")
	@Cacheable(value = "utilities", unless = "#result.size() == 0")
	public List<Utility> utilities()
	{
		final Set<String> filter = Stream.of("CUPON_PAGO", "EMPRESA DE PRUEBAS", "FONASA", "RECAUDA_REDFACIL")
				.collect(Collectors.toSet());
		return utilityPaymentClient.getUtilities()
				.orElseThrow(ServerErrorException::new)
				.stream()
				.filter(utility -> !filter.contains(utility.getName()))
				.collect(Collectors.toList());
	}

	@GetMapping("/v1/utilities/multicaja")
	@Cacheable(value = "utilities-multicaja", unless = "#result.equals(\"\")")
	public String utilitiesMulticaja()
	{
		return utilityPaymentClient.getUtilitiesMulticaja().orElseThrow(ServerErrorException::new);
	}
}
