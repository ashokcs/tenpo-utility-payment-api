package cl.tenpo.utility.payments.transaction;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long>
{
	public Optional<Transaction> findByPublicId(final String publicId);
	public Optional<Transaction> findByIdAndStatus(final Long id, final String status);
	public Optional<Transaction> findByPublicIdAndStatus(final String publicId, final String status);

    @Query(value = "SELECT nextval('public.transactions_seq')", nativeQuery = true)
    public Optional<Integer> getNextTransactionSequence();
}
