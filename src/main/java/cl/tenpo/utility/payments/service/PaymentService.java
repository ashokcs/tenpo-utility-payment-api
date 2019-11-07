package cl.tenpo.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.entity.Payment;
import cl.tenpo.utility.payments.repository.PaymentRepository;

@Service
public class PaymentService
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private final PaymentRepository paymentRepository;

	public PaymentService(final PaymentRepository paymentRepository)
	{
		this.paymentRepository = paymentRepository;
	}

	public Optional<Payment> save(final Payment payment)
	{
		try {
			return Optional.of(paymentRepository.save(payment));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
