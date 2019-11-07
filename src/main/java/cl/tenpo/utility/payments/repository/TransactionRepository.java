package cl.tenpo.utility.payments.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cl.tenpo.utility.payments.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, UUID>
{
    @Query(value = "SELECT nextval('public.transactions_seq')", nativeQuery = true)
    public Optional<Integer> getNextval();
    public Optional<Transaction> findByIdAndUser(final UUID id, final UUID user);
}
