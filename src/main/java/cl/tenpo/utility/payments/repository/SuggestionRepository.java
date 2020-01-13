package cl.tenpo.utility.payments.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Suggestion;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long>
{
	public List<Suggestion> findFirst20ByUserAndStatusAndExpiredGreaterThanOrderByCreatedDesc(final UUID user, final String status, final OffsetDateTime expired);
	public List<Suggestion> findAllByUserAndUtilityIdAndIdentifier(final UUID user, final Long utilityId, final String identifier);
}
