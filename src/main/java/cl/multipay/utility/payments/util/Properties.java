package cl.multipay.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	/* webpay */
	@Value("${webpay.init.url}") public String webpayInitUrl;
	@Value("${webpay.result.url}") public String webpayResultUrl;
	@Value("${webpay.result.ack}") public String webpayAckUrl;
	@Value("${webpay.return-url}") public String webpayReturnUrl;
	@Value("${webpay.final-url}") public String webpayFinalUrl;
	@Value("${webpay.commerce.user}") public String webpayCommerceUser;
	@Value("${webpay.commerce.pass}") public String webpayCommercePass;
	@Value("${webpay.commerce.env}") public String webpayCommerceEnv;
	@Value("${webpay.redirect.error}") public String webpayRedirectError;
	@Value("${webpay.redirect.error-order}") public String webpayRedirectErrorOrder;
	@Value("${webpay.redirect.final}") public String webpayRedirectFinal;

	/* eft */
	@Value("${eft.basic-auth}") public String eftBasicAuth;
	@Value("${eft.notify-basic-auth}") public String eftNotifyBasicAuth;
	@Value("${eft.commerce-id}") public String eftCommerceId;
	@Value("${eft.branch-id}") public String eftBranchId;
	@Value("${eft.get-order-status.url}") public String eftGetOrderStatusUrl;
	@Value("${eft.create-order.url}") public String eftCreateOrderUrl;
	@Value("${eft.create-order.description}") public String eftCreateOrderDescription;
	@Value("${eft.create-order.request-duration}") public int eftCreateOrderRequestDuration;
	@Value("${eft.create-order.go-back-url}") public String eftCreateOrderGoBackUrl;
	@Value("${eft.create-order.notify-url}") public String eftCreateOrderNotifyUrl;
	@Value("${eft.redirect.error}") public String eftRedirectError;
	@Value("${eft.redirect.error-order}") public String eftRedirectErrorOrder;
	@Value("${eft.redirect.final}") public String eftRedirectFinal;

	/* multicaja utilities */
	@Value("${multicaja.utilities.apikey}") public String multicajaUtilitiesApiKey;
	@Value("${multicaja.utilities.terminal}") public String multicajaUtilitiesTerminal;
	@Value("${multicaja.utilities.channel}") public String multicajaUtilitiesChannel;
	@Value("${multicaja.utilities.commerce}") public String multicajaUtilitiesCommerce;
	@Value("${multicaja.utilities.url}") public String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.bill.url}") public String multicajaUtlitiesBillUrl;

	/* proxy */
	@Value("${httpclient.proxy}") public String httpClientProxy;
	@Value("${httpclient.proxy.exclude}") public String httpClientProxyExclude;
	@Value("${httpclient.trust.all}") public boolean httpClientTrustAll;

	/* mail */
	@Value("${mail.sendgrid.api-key}") public String mailSendgridApiKey;
	@Value("${mail.utility-payments.receipt.from}") public String mailUtilityPaymentsReceiptFrom;
	@Value("${mail.utility-payments.receipt.from-name}") public String mailUtilityPaymentsReceiptFromName;
	@Value("${mail.utility-payments.receipt.bcc}") public String mailUtilityPaymentsReceiptBcc;
	@Value("${mail.utility-payments.receipt.subject}") public String mailUtilityPaymentsReceiptSubject;
	@Value("${mail.utility-payments.receipt.template}") public String mailUtilityPaymentsReceiptTemplate;
}
