package cl.tenpo.utility.payments.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.entity.Transaction;
import cl.tenpo.utility.payments.repository.TransactionRepository;
import cl.tenpo.utility.payments.util.Utils;

@Service
public class TransactionService
{
	private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

	private final TransactionRepository transactionRepository;

	public TransactionService(
		final TransactionRepository transactionRepository
	){
		this.transactionRepository = transactionRepository;
	}

	public Optional<Transaction> findById(final UUID id)
	{
		return transactionRepository.findById(id);
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
