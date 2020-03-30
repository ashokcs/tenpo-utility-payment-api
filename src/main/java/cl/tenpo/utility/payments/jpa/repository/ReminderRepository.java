package cl.tenpo.utility.payments.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long>
{
	public List<Reminder> findAllByUserAndExpiredGreaterThanOrderByCreatedAsc(final UUID userId, final OffsetDateTime expired);
}
