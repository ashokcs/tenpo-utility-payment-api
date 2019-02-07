package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.WebpayPayment;
import cl.multipay.utility.payments.repository.WebpayPaymentRepository;

@Service
public class PaymentService
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private final WebpayPaymentRepository webpayPaymentRepository;

	public PaymentService(final WebpayPaymentRepository webpayPaymentRepository)
	{
		this.webpayPaymentRepository =  webpayPaymentRepository;
	}

	public Optional<WebpayPayment> save(final WebpayPayment payment)
	{
		try {
			return Optional.of(webpayPaymentRepository.save(payment));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
