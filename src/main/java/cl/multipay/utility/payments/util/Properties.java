package cl.multipay.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	/* webpay */
	@Value("${webpay.init.url}") private String webpayInitUrl;
	@Value("${webpay.result.url}") private String webpayResultUrl;
	@Value("${webpay.result.ack}") private String webpayAckUrl;
	@Value("${webpay.return-url}") private String webpayReturnUrl;
	@Value("${webpay.final-url}") private String webpayFinalUrl;
	@Value("${webpay.commerce.user}") private String webpayCommerceUser;
	@Value("${webpay.commerce.pass}") private String webpayCommercePass;
	@Value("${webpay.commerce.env}") private String webpayCommerceEnv;
	@Value("${webpay.redirect.error}") private String webpayRedirectError;
	@Value("${webpay.redirect.error-order}") private String webpayRedirectErrorOrder;
	@Value("${webpay.redirect.final}") private String webpayRedirectFinal;

	/* eft */
	@Value("${eft.basic-auth}") private String eftBasicAuth;
	@Value("${eft.notify-basic-auth}") private String eftNotifyBasicAuth;
	@Value("${eft.commerce-id}") private String eftCommerceId;
	@Value("${eft.branch-id}") private String eftBranchId;
	@Value("${eft.get-order-status.url}") private String eftGetOrderStatusUrl;
	@Value("${eft.create-order.url}") private String eftCreateOrderUrl;
	@Value("${eft.create-order.description}") private String eftCreateOrderDescription;
	@Value("${eft.create-order.request-duration}") private int eftCreateOrderRequestDuration;
	@Value("${eft.create-order.go-back-url}") private String eftCreateOrderGoBackUrl;
	@Value("${eft.create-order.notify-url}") private String eftCreateOrderNotifyUrl;
	@Value("${eft.redirect.error}") private String eftRedirectError;
	@Value("${eft.redirect.error-order}") private String eftRedirectErrorOrder;
	@Value("${eft.redirect.final}") private String eftRedirectFinal;

	/* multicaja utilities */
	@Value("${multicaja.utilities.apikey}") private String multicajaUtilitiesApiKey;
	@Value("${multicaja.utilities.terminal}") private String multicajaUtilitiesTerminal;
	@Value("${multicaja.utilities.channel}") private String multicajaUtilitiesChannel;
	@Value("${multicaja.utilities.commerce}") private String multicajaUtilitiesCommerce;
	@Value("${multicaja.utilities.url}") private String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.bill.url}") private String multicajaUtlitiesBillUrl;

	/* proxy */
	@Value("${httpclient.proxy}") private String httpClientProxy;
	@Value("${httpclient.proxy.exclude}") private String httpClientProxyExclude;
	@Value("${httpclient.trust.all}") private boolean httpClientTrustAll;

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

	public String getEftCreateOrderUrl()
	{
		return eftCreateOrderUrl;
	}

	public String getEftBasicAuth()
	{
		return eftBasicAuth;
	}

	public String getEftCommerceId()
	{
		return eftCommerceId;
	}

	public String getEftBranchId()
	{
		return eftBranchId;
	}

	public String getEftCreateOrderDescription()
	{
		return eftCreateOrderDescription;
	}

	public int getEftCreateOrderRequestDuration()
	{
		return eftCreateOrderRequestDuration;
	}

	public String getEftCreateOrderGoBackUrl()
	{
		return eftCreateOrderGoBackUrl;
	}

	public String getEftCreateOrderNotifyUrl()
	{
		return eftCreateOrderNotifyUrl;
	}

	public String getHttpClientProxyExclude()
	{
		return httpClientProxyExclude;
	}

	public boolean isHttpClientTrustAll()
	{
		return httpClientTrustAll;
	}

	public String getEftRedirectError()
	{
		return eftRedirectError;
	}

	public String getEftRedirectErrorOrder()
	{
		return eftRedirectErrorOrder;
	}

	public String getEftGetOrderStatusUrl()
	{
		return eftGetOrderStatusUrl;
	}

	public String getEftRedirectFinal()
	{
		return eftRedirectFinal;
	}

	public String getEftNotifyBasicAuth()
	{
		return eftNotifyBasicAuth;
	}

	public String getMulticajaUtilitiesChannel()
	{
		return multicajaUtilitiesChannel;
	}

	public String getMulticajaUtilitiesApiKey()
	{
		return multicajaUtilitiesApiKey;
	}
}
