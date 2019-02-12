package cl.multipay.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	@Value("${webpay.init.url}")
	private String webpayInitUrl;

	@Value("${webpay.result.url}")
	private String webpayResultUrl;

	@Value("${webpay.result.ack}")
	private String webpayAckUrl;

	@Value("${webpay.return-url}")
	private String webpayReturnUrl;

	@Value("${webpay.final-url}")
	private String webpayFinalUrl;

	@Value("${webpay.commerce.user}")
	private String webpayCommerceUser;

	@Value("${webpay.commerce.pass}")
	private String webpayCommercePass;

	@Value("${webpay.commerce.env}")
	private String webpayCommerceEnv;

	@Value("${webpay.redirect.error}")
	private String webpayRedirectError;

	@Value("${webpay.redirect.error-order}")
	private String webpayRedirectErrorOrder;

	@Value("${webpay.redirect.final}")
	private String webpayRedirectFinal;

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

	public String getWebpayInitUrl()
	{
		return webpayInitUrl;
	}

	public String getWebpayReturnUrl()
	{
		return webpayReturnUrl;
	}

	public String getWebpayFinalUrl()
	{
		return webpayFinalUrl;
	}

	public String getWebpayCommerceUser()
	{
		return webpayCommerceUser;
	}

	public String getWebpayCommercePass()
	{
		return webpayCommercePass;
	}

	public String getWebpayCommerceEnv()
	{
		return webpayCommerceEnv;
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

	public String getWebpayRedirectError()
	{
		return webpayRedirectError;
	}

	public String getWebpayResultUrl()
	{
		return webpayResultUrl;
	}

	public String getWebpayAckUrl()
	{
		return webpayAckUrl;
	}

	public String getWebpayRedirectFinal()
	{
		return webpayRedirectFinal;
	}

	public String getWebpayRedirectErrorOrder()
	{
		return webpayRedirectErrorOrder;
	}
}
