package cl.multipay.utility.payments.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.repository.UtilityPaymentBillRepository;

@Service
public class UtilityPaymentBillService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentBillService.class);
	private final EntityManager entityManager;
	private final UtilityPaymentBillRepository utilityPaymentBillRepository;

	public UtilityPaymentBillService(final UtilityPaymentBillRepository utilityPaymentBillRepository,
		final EntityManager entityManager)
	{
		this.entityManager = entityManager;
		this.utilityPaymentBillRepository = utilityPaymentBillRepository;
	}

	@Transactional
	public Optional<UtilityPaymentBill> saveAndRefresh(final UtilityPaymentBill utilityPaymentBill)
	{
		try {
			final UtilityPaymentBill saved = utilityPaymentBillRepository.save(utilityPaymentBill);
			entityManager.refresh(saved);
			return Optional.of(saved);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentBill> save(final UtilityPaymentBill utilityPaymentBill)
	{
		try {
			return Optional.of(utilityPaymentBillRepository.save(utilityPaymentBill));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentBill> findByTransactionId(final Long transactionId)
	{
		try {
			return utilityPaymentBillRepository.findByTransactionId(transactionId);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<UtilityPaymentBill> getPendingByTransactionId(final Long transactionId)
	{
		try {
			return utilityPaymentBillRepository.findByTransactionIdAndStatus(transactionId, UtilityPaymentBill.PENDING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
