package cl.multipay.utility.payments.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.repository.UtilityPaymentTransactionRepository;

@Service
public class UtilityPaymentTransactionService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentTransactionService.class);

	private final EntityManager entityManager;
	private final UtilityPaymentTransactionRepository utilityPaymentTransactionRepository;

	public UtilityPaymentTransactionService(final EntityManager entityManager,
			final UtilityPaymentTransactionRepository utilityPaymentTransactionRepository)
	{
		this.entityManager = entityManager;
		this.utilityPaymentTransactionRepository = utilityPaymentTransactionRepository;
	}

	@Transactional
	public Optional<UtilityPaymentTransaction> saveAndRefresh(final UtilityPaymentTransaction bill)
	{
		try {
			final UtilityPaymentTransaction saved = utilityPaymentTransactionRepository.save(bill);
			entityManager.refresh(saved);
			return Optional.of(saved);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentTransaction> save(final UtilityPaymentTransaction bill)
	{
		try {
			return Optional.of(utilityPaymentTransactionRepository.save(bill));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentTransaction> findById(final Long id)
	{
		return utilityPaymentTransactionRepository.findById(id);
	}

	public Optional<UtilityPaymentTransaction> findByPublicId(final String publicId)
	{
		return utilityPaymentTransactionRepository.findByPublicId(publicId);
	}

	public Optional<UtilityPaymentTransaction> getPendingByPublicId(final String publicId)
	{
		return utilityPaymentTransactionRepository.findByPublicIdAndStatus(publicId, UtilityPaymentTransaction.PENDING);
	}

	public Optional<UtilityPaymentTransaction> getWaitingById(final Long id)
	{
		return utilityPaymentTransactionRepository.findByIdAndStatus(id, UtilityPaymentTransaction.WAITING);
	}

	public Optional<UtilityPaymentTransaction> getSucceedById(final Long id)
	{
		return utilityPaymentTransactionRepository.findByIdAndStatus(id, UtilityPaymentTransaction.SUCCEEDED);
	}

	public Optional<UtilityPaymentTransaction> getSucceedByPublicId(final String publicId)
	{
		return utilityPaymentTransactionRepository.findByPublicIdAndStatus(publicId, UtilityPaymentTransaction.SUCCEEDED);
	}
}
