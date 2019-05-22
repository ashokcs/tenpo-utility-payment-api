package cl.tenpo.utility.payments.transferencia;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long>
{
	public Optional<Transferencia> findByTransactionId(final Long transactionId);
	public Optional<Transferencia> findByPublicIdAndNotifyIdAndStatus(final String publicId, final String notifyId, final String status);
	public Optional<Transferencia> findByPublicId(final String publicId);
}
