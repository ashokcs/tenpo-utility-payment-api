package cl.tenpo.utility.payments.util.http;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

import cl.tenpo.utility.payments.event.SendReceipTransferenciaEvent;
import cl.tenpo.utility.payments.event.SendReceiptWebpayEvent;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.util.Properties;
import cl.tenpo.utility.payments.util.Utils;

@Service
public class SendgridClient
{
	private static final Logger logger = LoggerFactory.getLogger(SendgridClient.class);

	private final Properties properties;

	public SendgridClient(final Properties properties)
	{
		this.properties = properties;
	}

	public void sendReceipt(final SendReceiptWebpayEvent event)
	{
		try {
			final Transaction transaction = event.getTransaction();
			final Bill bill = event.getBill();
			final Webpay webpay = event.getWebpay();

			final Mail mail = getReceiptMail(transaction, properties.mailUtilityPaymentsReceiptWebpayTemplate);
			final Personalization personalization = mail.getPersonalization().get(0);

			final OffsetDateTime updated = transaction.getUpdated()
					.withOffsetSameInstant(ZoneOffset.of(properties.timezoneOffset));

			// add template data
		    personalization.addDynamicTemplateData("subject", properties.mailUtilityPaymentsReceiptSubject);
		    personalization.addDynamicTemplateData("transaction_utility", bill.getUtility().friendlyName());
		    personalization.addDynamicTemplateData("transaction_date", Utils.format("dd/MM/yyyy", updated));
		    personalization.addDynamicTemplateData("transaction_time", Utils.format("HH:mm", updated) + " hrs");
		    personalization.addDynamicTemplateData("transaction_identifier", bill.getIdentifier());
		    personalization.addDynamicTemplateData("transaction_order", transaction.getOrder().toString());
		    personalization.addDynamicTemplateData("transaction_auth", bill.getConfirmAuthCode());
		    personalization.addDynamicTemplateData("transaction_total", Utils.currency(transaction.getAmount()));

		    personalization.addDynamicTemplateData("payment_method", Utils.paymentMethodFriendlyName(transaction.getPaymentMethod()));
		    personalization.addDynamicTemplateData("payment_amount", Utils.currency(transaction.getAmount()));
		    personalization.addDynamicTemplateData("payment_auth", webpay.getAuthCode());
		    personalization.addDynamicTemplateData("payment_type", Utils.paymentTypeFriendlyName(webpay.getPaymentType()));
		    personalization.addDynamicTemplateData("payment_share_type", Utils.sharesTypeFriendlyName(webpay.getPaymentType()));
		    personalization.addDynamicTemplateData("payment_share_number", webpay.getSharesNumber());
		    personalization.addDynamicTemplateData("payment_share_card", webpay.getCardNumber());

		    // send email
		    sendReceipt(mail);
	    } catch (final Exception ex) {
	    	logger.error(ex.getMessage(), ex);
	    }
	}

	public void sendReceipt(final SendReceipTransferenciaEvent event)
	{
		try {
			final Transaction transaction = event.getTransaction();
			final Bill bill = event.getBill();
			final Transferencia utilityPaymentEft = event.getTransferencia();

			final Mail mail = getReceiptMail(transaction, properties.mailUtilityPaymentsReceiptEftTemplate);
			final Personalization personalization = mail.getPersonalization().get(0);

			final OffsetDateTime updated = transaction.getUpdated()
					.withOffsetSameInstant(ZoneOffset.of(properties.timezoneOffset));

			// add template data
		    personalization.addDynamicTemplateData("subject", properties.mailUtilityPaymentsReceiptSubject);
		    personalization.addDynamicTemplateData("transaction_utility", bill.getUtility().friendlyName());
		    personalization.addDynamicTemplateData("transaction_date", Utils.format("dd/MM/yyyy", updated));
		    personalization.addDynamicTemplateData("transaction_time", Utils.format("HH:mm", updated) + " hrs");
		    personalization.addDynamicTemplateData("transaction_identifier", bill.getIdentifier());
		    personalization.addDynamicTemplateData("transaction_order", transaction.getOrder().toString());
		    personalization.addDynamicTemplateData("transaction_auth", bill.getConfirmAuthCode());
		    personalization.addDynamicTemplateData("transaction_total", Utils.currency(transaction.getAmount()));

		    personalization.addDynamicTemplateData("payment_method", Utils.paymentMethodFriendlyName(transaction.getPaymentMethod()));
		    personalization.addDynamicTemplateData("payment_amount", Utils.currency(transaction.getAmount()));
		    personalization.addDynamicTemplateData("payment_order", utilityPaymentEft.getOrder());

		    // send email
		    sendReceipt(mail);
	    } catch (final Exception ex) {
	    	logger.error(ex.getMessage(), ex);
	    }
	}

	private Mail getReceiptMail(final Transaction utilityPaymentTransaction, final String template)
	{
		final Email from = new Email(properties.mailUtilityPaymentsReceiptFrom, properties.mailUtilityPaymentsReceiptFromName);
		final Mail mail = new Mail();
		mail.setSubject(properties.mailUtilityPaymentsReceiptSubject);
		mail.setFrom(from);
		mail.setTemplateId(template);
		final Personalization personalization = new Personalization();
		mail.addPersonalization(personalization);

		// add to
		personalization.addTo(new Email(utilityPaymentTransaction.getEmail()));

		// add bcc
		if ((properties.mailUtilityPaymentsReceiptBcc != null) && !properties.mailUtilityPaymentsReceiptBcc.isEmpty()) {
			final String[] bccs = properties.mailUtilityPaymentsReceiptBcc.split("\\,");
			for (final String bcc : bccs) {
				if (utilityPaymentTransaction.getEmail().equals(bcc.trim())) continue;
				personalization.addBcc(new Email(bcc.trim()));
			}
		}

		return mail;
	}

	private void sendReceipt(final Mail mail) throws IOException
	{
		final SendGrid sg = new SendGrid(properties.mailSendgridApiKey);
	    final Request request = new Request();
    	request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        logger.info("=> Sendgrid Notification: {}: {}", properties.mailUtilityPaymentsReceiptEftTemplate, request.getEndpoint());
        final Response response = sg.api(request);
        logger.info("<= Sendgrid Notification: {}: {} [{}]", properties.mailUtilityPaymentsReceiptEftTemplate, request.getEndpoint(), response.getStatusCode());
	}
}
