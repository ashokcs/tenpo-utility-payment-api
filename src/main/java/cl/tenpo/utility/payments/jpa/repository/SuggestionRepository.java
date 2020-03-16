package cl.tenpo.utility.payments.jpa.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Suggestion;

public interface SuggestionRepository extends JpaRepository<Suggestion, Long>
{
	public List<Suggestion> findFirst20ByUserAndStatusAndExpiredGreaterThanOrderByCreatedAsc(final UUID user, final String status, final OffsetDateTime expired);
	public List<Suggestion> findAllByUserAndUtilityIdAndIdentifier(final UUID user, final Long utilityId, final String identifier);
	public Optional<Suggestion> findByUserAndId(final UUID user, final Long id);
}
