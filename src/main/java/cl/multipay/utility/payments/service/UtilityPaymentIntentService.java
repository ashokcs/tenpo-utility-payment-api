package cl.multipay.utility.payments.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentIntent;
import cl.multipay.utility.payments.repository.UtilityPaymentIntentRepository;

@Service
public class UtilityPaymentIntentService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentIntentService.class);

	private final UtilityPaymentIntentRepository utilityPaymentIntentRepository;
	private final EntityManager entityManager;

	public UtilityPaymentIntentService(final UtilityPaymentIntentRepository upir, final EntityManager em)
	{
		utilityPaymentIntentRepository = upir;
		entityManager = em;
	}

	@Transactional
	public boolean save(final UtilityPaymentIntent utilityPaymentIntent)
	{
		try {
			utilityPaymentIntentRepository.save(utilityPaymentIntent);
			entityManager.refresh(utilityPaymentIntent);
			return true;
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	public UtilityPaymentIntent findByUuid(final String uuid)
	{
		return utilityPaymentIntentRepository.findByUuid(uuid);
	}
}
