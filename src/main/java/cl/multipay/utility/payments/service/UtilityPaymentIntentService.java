package cl.multipay.utility.payments.service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentIntent;
import cl.multipay.utility.payments.repository.UtilityPaymentIntentRepository;

@Service
public class UtilityPaymentIntentService
{
	private final UtilityPaymentIntentRepository utilityPaymentIntentRepository;
	private final EntityManager entityManager;

	public UtilityPaymentIntentService(final UtilityPaymentIntentRepository upir, final EntityManager em)
	{
		utilityPaymentIntentRepository = upir;
		entityManager = em;
	}

	@Transactional
	public void save(final UtilityPaymentIntent utilityPaymentIntent)
	{
		utilityPaymentIntentRepository.save(utilityPaymentIntent);
		entityManager.refresh(utilityPaymentIntent);
	}

	public UtilityPaymentIntent findByUuid(final String uuid)
	{
		return utilityPaymentIntentRepository.findByUuid(uuid);
	}
}
