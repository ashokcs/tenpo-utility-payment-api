package cl.tenpo.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.jpa.repository.TransferenciaRepository;

@Service
public class TransferenciaService
{
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

	private final TransferenciaRepository tr;

	public TransferenciaService(final TransferenciaRepository tr)
	{
		this.tr =  tr;
	}

	public Optional<Transferencia> save(final Transferencia transferencia)
	{
		try {
			return Optional.of(tr.save(transferencia));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Transferencia> findByTransactionId(final Long transactionId)
	{
		try {
			return tr.findByTransactionId(transactionId);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Transferencia> getWaitingOrPaidByPublicIdAndNotifyId(final String publicId, final String notifyId)
	{
		try {
			final Optional<Transferencia> pen = tr.findByPublicIdAndNotifyIdAndStatus(publicId, notifyId, Transferencia.WAITING);
			return pen.isPresent() ? pen : tr.findByPublicIdAndNotifyIdAndStatus(publicId, notifyId, Transferencia.PAID);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Transferencia> findByPublicId(final String publicId)
	{
		try {
			return tr.findByPublicId(publicId);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
