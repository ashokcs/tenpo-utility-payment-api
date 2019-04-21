package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Transferencia;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long>
{
	public Optional<Transferencia> findByTransactionId(final Long transactionId);
	public Optional<Transferencia> findByPublicIdAndNotifyIdAndStatus(final String publicId, final String notifyId, final String status);
	public Optional<Transferencia> findByPublicId(final String publicId);
}
