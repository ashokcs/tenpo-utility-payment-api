package cl.tenpo.utility.payments.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, UUID>
{
	public Optional<Bill> findByIdAndUserAndStatus(final UUID id, final UUID user, final String status);
	public Optional<Bill> findFirstByIdentifierAndUtilityIdAndUserAndStatusAndCreatedGreaterThanOrderByCreatedDesc(final String identifier, final Long utilityId, final UUID user, final String status, final OffsetDateTime created);
	public Optional<Bill> findByIdAndUser(final UUID id, final UUID user);
	public List<Bill> findByUserAndTransactionId(final UUID user, final UUID transactionId);
}
