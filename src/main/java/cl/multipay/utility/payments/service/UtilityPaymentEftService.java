package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentEft;
import cl.multipay.utility.payments.repository.UtilityPaymentEftRepository;

@Service
public class UtilityPaymentEftService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentEftService.class);

	private final UtilityPaymentEftRepository uper;;

	public UtilityPaymentEftService(final UtilityPaymentEftRepository uper)
	{
		this.uper =  uper;
	}

	public Optional<UtilityPaymentEft> save(final UtilityPaymentEft transferenciaPayment)
	{
		try {
			return Optional.of(uper.save(transferenciaPayment));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentEft> findByPublicId(final String publicId)
	{
		try {
			return uper.findByPublicId(publicId);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentEft> getPendingByPublicId(final String publicId)
	{
		try {
			return uper.findByPublicIdAndStatus(publicId, UtilityPaymentEft.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentEft> getPendingByPublicIdAndNotifyId(final String publicId, final String notifyId)
	{
		try {
			return uper.findByPublicIdAndNotifyIdAndStatus(publicId, notifyId, UtilityPaymentEft.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
