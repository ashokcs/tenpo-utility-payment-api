package cl.multipay.utility.payments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.Payment;
import cl.multipay.utility.payments.repository.PaymentRepository;

@Service
public class PaymentService
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

	private final PaymentRepository paymentRepository;

	public PaymentService(final PaymentRepository paymentRepository)
	{
		this.paymentRepository =  paymentRepository;
	}

	public boolean save(final Payment payment)
	{
		try {
			paymentRepository.save(payment);
			return true;
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
}
