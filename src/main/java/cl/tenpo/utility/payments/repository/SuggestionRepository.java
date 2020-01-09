package cl.tenpo.utility.payments.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Suggestion;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long>
{
	List<Suggestion> findAllByUserAndStatusAndExpiredGreaterThanOrderByCreatedDesc(final UUID user, final String status, final OffsetDateTime expired);
}
