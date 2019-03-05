package cl.multipay.utility.payments.util;

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
	@Value("${eft.front.error}") public String eftFrontError;
	@Value("${eft.front.error-order}") public String eftFrontErrorOrder;
	@Value("${eft.front.final}") public String eftFrontFinal;

	/* multicaja utilities */
	@Value("${multicaja.utilities.apikey}") public String multicajaUtilitiesApiKey;
	@Value("${multicaja.utilities.password}") public String multicajaUtilitiesPassword;
	@Value("${multicaja.utilities.url}") public String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.bill.url}") public String multicajaUtlitiesBillUrl;
	@Value("${multicaja.utilities.bill-confirm.url}") public String multicajaUtlitiesBillConfirmUrl;

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
	@Value("${mail.utility-payments.receipt.webpay.template}") public String mailUtilityPaymentsReceiptWebpayTemplate;
	@Value("${mail.utility-payments.receipt.eft.template}") public String mailUtilityPaymentsReceiptEftTemplate;

	/* recaptcha */
	@Value("${recaptcha.verify.url}") public String recaptchaVerifyUrl;
	@Value("${recaptcha.secret}") public String recaptchaSecret;
}
