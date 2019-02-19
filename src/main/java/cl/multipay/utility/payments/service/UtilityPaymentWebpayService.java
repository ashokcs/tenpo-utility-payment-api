package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;
import cl.multipay.utility.payments.repository.UtilityPaymentWebpayRepository;

@Service
public class UtilityPaymentWebpayService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentWebpayService.class);

	private final UtilityPaymentWebpayRepository upwr;

	public UtilityPaymentWebpayService(final UtilityPaymentWebpayRepository upwr)
	{
		this.upwr =  upwr;
	}

	public Optional<UtilityPaymentWebpay> save(final UtilityPaymentWebpay payment)
	{
		try {
			return Optional.of(upwr.save(payment));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentWebpay> getPendingByToken(final String token)
	{
		try {
			return upwr.findByTokenAndStatus(token, UtilityPaymentWebpay.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentWebpay> getAckByToken(final String token)
	{
		try {
			return upwr.findByTokenAndStatus(token, UtilityPaymentWebpay.ACKNOWLEDGED);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
