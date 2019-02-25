package cl.multipay.utility.payments.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;
import cl.multipay.utility.payments.util.Properties;
import cl.multipay.utility.payments.util.Utils;

@Service
public class SendgridClient
{
	private static final Logger logger = LoggerFactory.getLogger(SendgridClient.class);

	private final Properties properties;
	private final Utils utils;

	public SendgridClient(final Properties properties, final Utils utils)
	{
		this.properties = properties;
		this.utils = utils;
	}

	public void sendReceipt(final UtilityPaymentTransaction utilityPaymentTransaction,
		final UtilityPaymentBill utilityPaymentBill, final UtilityPaymentWebpay utilityPaymentWebpay)
	{
		try {
			final Email from = new Email(properties.mailUtilityPaymentsReceiptFrom, properties.mailUtilityPaymentsReceiptFromName);
			final Mail mail = new Mail();
			mail.setSubject(properties.mailUtilityPaymentsReceiptSubject);
			mail.setFrom(from);
			mail.setTemplateId(properties.mailUtilityPaymentsReceiptTemplate);
			final Personalization personalization = new Personalization();
			mail.addPersonalization(personalization);

			// add to
			personalization.addTo(new Email(utilityPaymentTransaction.getEmail()));

			// add bcc
			if ((properties.mailUtilityPaymentsReceiptBcc != null) && !properties.mailUtilityPaymentsReceiptBcc.isEmpty()) {
				final String[] bccs = properties.mailUtilityPaymentsReceiptBcc.split("\\,");
				for (final String bcc : bccs) {
					if (utilityPaymentTransaction.getEmail().equals(bcc)) continue;
					personalization.addTo(new Email(bcc.trim()));
				}
			}

			// add template data
		    personalization.addDynamicTemplateData("subject", properties.mailUtilityPaymentsReceiptSubject);
		    personalization.addDynamicTemplateData("transaction_utility", utilityPaymentBill.getUtility());
		    personalization.addDynamicTemplateData("transaction_date", utils.format("dd/MM/yyyy", utilityPaymentTransaction.getUpdated()));
		    personalization.addDynamicTemplateData("transaction_time", utils.format("HH:mm", utilityPaymentTransaction.getUpdated()) + " hrs");
		    personalization.addDynamicTemplateData("transaction_identifier", utilityPaymentBill.getIdentifier());
		    personalization.addDynamicTemplateData("transaction_total", utilityPaymentTransaction.getAmount()); // TODO format
		    personalization.addDynamicTemplateData("transaction_order", utilityPaymentTransaction.getBuyOrder());
		    personalization.addDynamicTemplateData("transaction_auth", "123123123"); // TODO

		    personalization.addDynamicTemplateData("payment_method", utils.paymentMethod(utilityPaymentTransaction.getPaymentMethod()));
		    personalization.addDynamicTemplateData("payment_amount", utilityPaymentTransaction.getAmount());
		    personalization.addDynamicTemplateData("payment_auth", utilityPaymentWebpay.getAuthCode());
		    personalization.addDynamicTemplateData("payment_type", utils.paymentType(utilityPaymentWebpay.getPaymentType()));
		    personalization.addDynamicTemplateData("payment_share_type", utils.sharesType(utilityPaymentWebpay.getPaymentType()));
		    personalization.addDynamicTemplateData("payment_share_number", utilityPaymentWebpay.getShares());
		    personalization.addDynamicTemplateData("payment_share_card", utilityPaymentWebpay.getCard());

		    // send email
		    final SendGrid sg = new SendGrid(properties.mailSendgridApiKey);
		    final Request request = new Request();
	    	request.setMethod(Method.POST);
	        request.setEndpoint("mail/send");
	        request.setBody(mail.build());
	        logger.info("=> Sendgrid Notification: {}: {}", properties.mailUtilityPaymentsReceiptTemplate, request.getEndpoint());
	        final Response response = sg.api(request);
	        logger.info("<= Sendgrid Notification: {}: {} [{}]", properties.mailUtilityPaymentsReceiptTemplate, request.getEndpoint(), response.getStatusCode());
	    } catch (final Exception ex) {
	    	logger.error(ex.getMessage(), ex);
	    }
	}
}
