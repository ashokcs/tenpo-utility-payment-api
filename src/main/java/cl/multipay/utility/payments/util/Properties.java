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
	@Value("${transferencia.create-order.url}") private String transferenciaCreateOrderUrl;
	@Value("${transferencia.create-order.basic-auth}") private String transferenciaCreateOrderBasicAuth;
	@Value("${transferencia.create-order.commerce-id}") private String transferenciaCreateOrderCommerceId;
	@Value("${transferencia.create-order.branch-id}") private String transferenciaCreateOrderBranchId;
	@Value("${transferencia.create-order.description}") private String transferenciaCreateOrderDescription;
	@Value("${transferencia.create-order.request-duration}") private int transferenciaCreateOrderRequestDuration;
	@Value("${transferencia.create-order.go-back-url}") private String transferenciaCreateOrderGoBackUrl;
	@Value("${transferencia.create-order.notify-url}") private String transferenciaCreateOrderNotifyUrl;

	/* Multicaja Utilities */

	@Value("${multicaja.utilities.terminal}") private String multicajaUtilitiesTerminal;
	@Value("${multicaja.utilities.commerce}") private String multicajaUtilitiesCommerce;
	@Value("${multicaja.utilities.url}") private String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.bill.url}") private String multicajaUtlitiesBillUrl;
	@Value("${httpclient.proxy}") private String httpClientProxy;

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

	public String getTransferenciaCreateOrderBasicAuth()
	{
		return transferenciaCreateOrderBasicAuth;
	}

	public String getTransferenciaCreateOrderCommerceId()
	{
		return transferenciaCreateOrderCommerceId;
	}

	public String getTransferenciaCreateOrderBranchId()
	{
		return transferenciaCreateOrderBranchId;
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
}
