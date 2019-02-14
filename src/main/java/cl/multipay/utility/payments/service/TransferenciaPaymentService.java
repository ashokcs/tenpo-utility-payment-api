package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.TransferenciaPayment;
import cl.multipay.utility.payments.repository.TransferenciaPaymentRepository;

@Service
public class TransferenciaPaymentService
{
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaPaymentService.class);

	private final TransferenciaPaymentRepository transferenciaPaymentRepository;

	public TransferenciaPaymentService(final TransferenciaPaymentRepository transferenciaPaymentRepository)
	{
		this.transferenciaPaymentRepository =  transferenciaPaymentRepository;
	}

	public Optional<TransferenciaPayment> save(final TransferenciaPayment transferenciaPayment)
	{
		try {
			return Optional.of(transferenciaPaymentRepository.save(transferenciaPayment));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<TransferenciaPayment> findByPublicId(final String publicId)
	{
		try {
			return transferenciaPaymentRepository.findByPublicId(publicId);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<TransferenciaPayment> getPendingByPublicId(final String publicId)
	{
		try {
			return transferenciaPaymentRepository.findByPublicIdAndStatus(publicId, TransferenciaPayment.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<TransferenciaPayment> getPendingByPublicIdAndNotifyId(final String publicId, final String notifyId)
	{
		try {
			return transferenciaPaymentRepository.findByPublicIdAndNotifyIdAndStatus(publicId, notifyId, TransferenciaPayment.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
