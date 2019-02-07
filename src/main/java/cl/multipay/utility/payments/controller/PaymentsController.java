package cl.multipay.utility.payments.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.util.Properties;

@RestController
public class PaymentsController
{
	private final Properties properties;

	public PaymentsController(final Properties properties)
	{
		this.properties = properties;
	}

	@PostMapping("/v1/payments/webpay/return")
	public String webpayReturn(@RequestParam("token_ws") final String tokenWs)
	{
		if (isValidToken(tokenWs)) {
			System.out.println("--> " +  tokenWs);
		}
		return "redirect:" + properties.getWebpayRedirectError();
	}

	@PostMapping("/v1/payments/webpay/final")
	public void webpayFinal()
	{

	}

	private boolean isValidToken(final String token)
	{
		return (token != null) && token.matches("[0-9a-f]{64}");
	}
}
