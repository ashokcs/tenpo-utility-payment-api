package cl.multipay.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	@Value("${multicaja.payment.url}")
	private String multicajaPaymentUrl;

	@Value("${multicaja.payment.api-key}")
	private String multicajaPaymentApiKey;

	@Value("${multicaja.payment.description}")
	private String multicajaPaymentDescription;

	@Value("${multicaja.payment.currency}")
	private String multicajaPaymentCurrency;

	@Value("${multicaja.payment.return-url}")
	private String multicajaPaymentReturnUrl;

	@Value("${multicaja.payment.cancel-url}")
	private String multicajaPaymentCancelUrl;

	@Value("${multicaja.payment.confirm-url}")
	private String multicajaPaymentConfirmUrl;

	@Value("${multicaja.utilities.terminal}")
	private String multicajaUtilitiesTerminal;

	@Value("${multicaja.utilities.commerce}")
	private String multicajaUtilitiesCommerce;

	@Value("${multicaja.utilities.url}")
	private String multicajaUtilitiesUrl;

	@Value("${multicaja.utilities.bill.url}")
	private String multicajaUtlitiesBillUrl;

	@Value("${httpclient.proxy}")
	private String httpClientProxy;

	public String getMulticajaPaymentUrl()
	{
		return multicajaPaymentUrl;
	}

	public String getMulticajaPaymentApiKey()
	{
		return multicajaPaymentApiKey;
	}

	public String getMulticajaPaymentDescription()
	{
		return multicajaPaymentDescription;
	}

	public String getMulticajaPaymentCurrency()
	{
		return multicajaPaymentCurrency;
	}

	public String getMulticajaPaymentReturnUrl()
	{
		return multicajaPaymentReturnUrl;
	}

	public String getMulticajaPaymentCancelUrl()
	{
		return multicajaPaymentCancelUrl;
	}

	public String getMulticajaPaymentConfirmUrl()
	{
		return multicajaPaymentConfirmUrl;
	}

	public String getHttpClientProxy()
	{
		return httpClientProxy;
	}

	public String getMulticajaUtilitiesUrl()
	{
		return multicajaUtilitiesUrl;
	}

	public String getMulticajaUtlitiesBillUrl()
	{
		return multicajaUtlitiesBillUrl;
	}

	public String getMulticajaUtilitiesTerminal()
	{
		return multicajaUtilitiesTerminal;
	}

	public String getMulticajaUtilitiesCommerce()
	{
		return multicajaUtilitiesCommerce;
	}
}
