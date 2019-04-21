package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
	public Optional<Transaction> findByPublicId(final String publicId);
	public Optional<Transaction> findByIdAndStatus(final Long id, final String status);
	public Optional<Transaction> findByPublicIdAndStatus(final String publicId, final String status);
}
