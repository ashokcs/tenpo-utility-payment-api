package cl.tenpo.utility.payments.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.newrelic.api.agent.NewRelic;

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

	public Optional<Transaction> findByIdAndUser(final UUID id, final UUID user)
	{
		return transactionRepository.findByIdAndUser(id, user);
	}

	public Optional<Transaction> save(final Transaction transaction)
	{
		try {
			return Optional.of(transactionRepository.save(transaction));
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<String> getNextval()
	{
		try {
			final Integer seq = transactionRepository.getNextval().get();
			final LocalDateTime localDateTime = LocalDateTime.now();
			final String now = Utils.orderFormatter.format(localDateTime);
			final String order = "U" + now + String.format("%03d", seq);
			return Optional.of(order);
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
