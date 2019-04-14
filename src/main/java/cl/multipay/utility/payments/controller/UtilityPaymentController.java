package cl.multipay.utility.payments.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.Utility;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.http.UtilityPaymentClient;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class UtilityPaymentController
{
	private final UtilityPaymentClient utilityPaymentClient;

	public UtilityPaymentController(final UtilityPaymentClient multicajaService)
	{
		this.utilityPaymentClient = multicajaService;
	}

	@GetMapping("/v1/utilities")
	@Cacheable(value = "utilities", unless = "#result.size() == 0")
	public List<Utility> get()
	{
		final Set<String> filter = Stream.of("CUPON_PAGO", "EMPRESA DE PRUEBAS", "FONASA", "RECAUDA_REDFACIL")
				.collect(Collectors.toSet());
		return utilityPaymentClient.getUtilities()
				.orElseThrow(ServerErrorException::new)
				.stream()
				.filter(utility -> !filter.contains(utility.getUtility()))
				.collect(Collectors.toList());
	}

	@GetMapping("/v1/utilities/multicaja")
	@Cacheable(value = "utilities-multicaja", unless = "#result.equals(\"\")")
	public String getRaw()
	{
		return utilityPaymentClient.getUtilitiesMulticaja().orElseThrow(ServerErrorException::new);
	}
}
