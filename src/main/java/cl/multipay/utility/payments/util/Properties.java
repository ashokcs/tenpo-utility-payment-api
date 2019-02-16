package cl.multipay.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	/* Webpay */
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

	/* Transferencia */
	@Value("${transferencia.basic-auth}") private String transferenciaBasicAuth;
	@Value("${transferencia.notify-basic-auth}") private String transferenciaNotifyBasicAuth;
	@Value("${transferencia.commerce-id}") private String transferenciaCommerceId;
	@Value("${transferencia.branch-id}") private String transferenciaBranchId;
	@Value("${transferencia.get-order-status.url}") private String transferenciaGetOrderStatusUrl;
	@Value("${transferencia.create-order.url}") private String transferenciaCreateOrderUrl;
	@Value("${transferencia.create-order.description}") private String transferenciaCreateOrderDescription;
	@Value("${transferencia.create-order.request-duration}") private int transferenciaCreateOrderRequestDuration;
	@Value("${transferencia.create-order.go-back-url}") private String transferenciaCreateOrderGoBackUrl;
	@Value("${transferencia.create-order.notify-url}") private String transferenciaCreateOrderNotifyUrl;
	@Value("${transferencia.redirect.error}") private String transferenciaRedirectError;
	@Value("${transferencia.redirect.error-order}") private String transferenciaRedirectErrorOrder;
	@Value("${transferencia.redirect.final}") private String transferenciaRedirectFinal;

	/* Multicaja Utilities */
	@Value("${multicaja.utilities.terminal}") private String multicajaUtilitiesTerminal;
	@Value("${multicaja.utilities.channel}") private String multicajaUtilitiesChannel;
	@Value("${multicaja.utilities.commerce}") private String multicajaUtilitiesCommerce;
	@Value("${multicaja.utilities.url}") private String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.bill.url}") private String multicajaUtlitiesBillUrl;

	/* Proxy */
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

	public String getTransferenciaCreateOrderUrl()
	{
		return transferenciaCreateOrderUrl;
	}

	public String getTransferenciaBasicAuth()
	{
		return transferenciaBasicAuth;
	}

	public String getTransferenciaCommerceId()
	{
		return transferenciaCommerceId;
	}

	public String getTransferenciaBranchId()
	{
		return transferenciaBranchId;
	}

	public String getTransferenciaCreateOrderDescription()
	{
		return transferenciaCreateOrderDescription;
	}

	public int getTransferenciaCreateOrderRequestDuration()
	{
		return transferenciaCreateOrderRequestDuration;
	}

	public String getTransferenciaCreateOrderGoBackUrl()
	{
		return transferenciaCreateOrderGoBackUrl;
	}

	public String getTransferenciaCreateOrderNotifyUrl()
	{
		return transferenciaCreateOrderNotifyUrl;
	}

	public String getHttpClientProxyExclude()
	{
		return httpClientProxyExclude;
	}

	public boolean isHttpClientTrustAll()
	{
		return httpClientTrustAll;
	}

	public String getTransferenciaRedirectError()
	{
		return transferenciaRedirectError;
	}

	public String getTransferenciaRedirectErrorOrder()
	{
		return transferenciaRedirectErrorOrder;
	}

	public String getTransferenciaGetOrderStatusUrl()
	{
		return transferenciaGetOrderStatusUrl;
	}

	public String getTransferenciaRedirectFinal()
	{
		return transferenciaRedirectFinal;
	}

	public String getTransferenciaNotifyBasicAuth()
	{
		return transferenciaNotifyBasicAuth;
	}

	public String getMulticajaUtilitiesChannel()
	{
		return multicajaUtilitiesChannel;
	}
}
