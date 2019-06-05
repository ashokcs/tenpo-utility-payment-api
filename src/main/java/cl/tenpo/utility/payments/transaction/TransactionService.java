package cl.tenpo.utility.payments.transaction;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.util.Utils;

@Service
public class TransactionService
{
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	private final EntityManager entityManager;
	private final TransactionRepository transactionRepository;

	public TransactionService(
		final EntityManager entityManager,
		final TransactionRepository transactionRepository
	){
		this.entityManager = entityManager;
		this.transactionRepository = transactionRepository;
	}

	public Optional<Transaction> save(final Transaction transaction)
	{
		try {
			return Optional.of(transactionRepository.save(transaction));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	@Transactional
	public Optional<Transaction> saveAndRefresh(final Transaction transaction)
	{
		try {
			final Transaction saved = transactionRepository.save(transaction);
			entityManager.refresh(saved);
			return Optional.of(saved);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Transaction> findByPublicId(final String publicId)
	{
		return transactionRepository.findByPublicId(publicId);
	}

	public Optional<Transaction> getWaitingById(final Long id)
	{
		return transactionRepository.findByIdAndStatus(id, Transaction.WAITING);
	}

	public Optional<Transaction> findById(final Long id)
	{
		return transactionRepository.findById(id);
	}

	public Optional<Transaction> getSucceedByPublicId(final String publicId)
	{
		return transactionRepository.findByPublicIdAndStatus(publicId, Transaction.SUCCEEDED);
	}

	public Optional<Transaction> getPendingByPublicId(final String publicId)
	{
		return transactionRepository.findByPublicIdAndStatus(publicId, Transaction.PENDING);
	}

	public Optional<Transaction> getCreatedOrPendingByPublicId(final String publicId)
	{
		final Optional<Transaction> opt = transactionRepository.findByPublicIdAndStatus(publicId, Transaction.CREATED);
		return opt.isPresent() ? opt : transactionRepository.findByPublicIdAndStatus(publicId, Transaction.PENDING);
	}

	public Optional<String> generateOrderSequence()
	{
		try {
			final Integer seq = transactionRepository.getNextTransactionSequence().get();
			final LocalDateTime localDateTime = LocalDateTime.now();
			final String now = Utils.orderFormatter.format(localDateTime);
			final String order = "U" + now + String.format("%03d", seq);
			return Optional.of(order);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
