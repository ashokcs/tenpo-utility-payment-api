package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.WebpayPayment;
import cl.multipay.utility.payments.repository.WebpayPaymentRepository;

@Service
public class WebpayPaymentService
{
	private static final Logger logger = LoggerFactory.getLogger(WebpayPaymentService.class);

	private final WebpayPaymentRepository webpayPaymentRepository;

	public WebpayPaymentService(final WebpayPaymentRepository webpayPaymentRepository)
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

	public Optional<WebpayPayment> getPendingByToken(final String token)
	{
		try {
			return webpayPaymentRepository.findByTokenAndStatus(token, WebpayPayment.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<WebpayPayment> getAckByToken(final String token)
	{
		try {
			return webpayPaymentRepository.findByTokenAndStatus(token, WebpayPayment.ACK);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
