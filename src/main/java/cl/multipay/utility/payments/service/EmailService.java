package cl.multipay.utility.payments.service;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService
{
	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	@Autowired
    private JavaMailSender javaMailSender;

	@Async
	public void utilityPaymentReceipt()
	{
		try {
			final MimeMessage message = javaMailSender.createMimeMessage();
			final MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(new InternetAddress("test@multipay.cl"));
            helper.setTo(InternetAddress.parse("carlos.izquierdo@multicaja.cl"));
            helper.setBcc(InternetAddress.parse("carlos.izquierdo@multicaja.cl"));
            helper.setSubject("Multipay receipt subject");
            helper.setText("<h1>Receipt</h1>", true);
            javaMailSender.send(message);
            // TODO Properties
            // TODO templating
		} catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
	}
}
