package cl.tenpo.utility.payments.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, UUID>
{
	public Optional<Bill> findByIdAndStatus(final UUID id, final String status);
	public List<Bill> findByTransactionId(final UUID transactionId);
}
