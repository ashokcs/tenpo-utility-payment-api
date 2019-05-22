package cl.tenpo.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	@Value("${time-zone.offset}") public String timezoneOffset;

	/* webpay */
	@Value("${webpay.init.url}") public String webpayInitUrl;
	@Value("${webpay.result.url}") public String webpayResultUrl;
	@Value("${webpay.result.ack}") public String webpayAckUrl;
	@Value("${webpay.return-url}") public String webpayReturnUrl;
	@Value("${webpay.final-url}") public String webpayFinalUrl;
	@Value("${webpay.commerce.user}") public String webpayCommerceUser;
	@Value("${webpay.commerce.pass}") public String webpayCommercePass;
	@Value("${webpay.commerce.env}") public String webpayCommerceEnv;
	@Value("${webpay.front.error}") public String webpayFrontError;
	@Value("${webpay.front.error-order}") public String webpayFrontErrorOrder;
	@Value("${webpay.front.final}") public String webpayFrontFinal;

	/* transferencia */
	@Value("${transferencia.basic-auth}") public String transferenciaBasicAuth;
	@Value("${transferencia.notify-basic-auth}") public String transferenciaNotifyBasicAuth;
	@Value("${transferencia.commerce-id}") public String transferenciaCommerceId;
	@Value("${transferencia.branch-id}") public String transferenciaBranchId;
	@Value("${transferencia.get-order-status.url}") public String transferenciaGetOrderStatusUrl;
	@Value("${transferencia.create-order.url}") public String transferenciaCreateOrderUrl;
	@Value("${transferencia.create-order.description}") public String transferenciaCreateOrderDescription;
	@Value("${transferencia.create-order.request-duration}") public int transferenciaCreateOrderRequestDuration;
	@Value("${transferencia.create-order.go-back-url}") public String transferenciaCreateOrderGoBackUrl;
	@Value("${transferencia.create-order.notify-url}") public String transferenciaCreateOrderNotifyUrl;
	@Value("${transferencia.front.error}") public String transferenciaFrontError;
	@Value("${transferencia.front.error-order}") public String transferenciaFrontErrorOrder;
	@Value("${transferencia.front.final}") public String transferenciaFrontFinal;

	/* multicaja */
	@Value("${multicaja.utilities.apikey}") public String multicajaUtilitiesApiKey;
	@Value("${multicaja.utilities.url}") public String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.debt.url}") public String multicajaUtlitiesDebtUrl;
	@Value("${multicaja.utilities.confirm.url}") public String multicajaUtlitiesConfirmUrl;

	/* proxy */
	@Value("${httpclient.proxy}") public String httpClientProxy;
	@Value("${httpclient.proxy.exclude}") public String httpClientProxyExclude;
	@Value("${httpclient.trust.all}") public boolean httpClientTrustAll;
}
