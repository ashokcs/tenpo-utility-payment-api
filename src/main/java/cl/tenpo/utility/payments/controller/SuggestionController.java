package cl.tenpo.utility.payments.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.entity.Suggestion;
import cl.tenpo.utility.payments.repository.SuggestionRepository;
import cl.tenpo.utility.payments.repository.UtilityRepository;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class SuggestionController
{
	private final SuggestionRepository suggestionRepository;
	private final UtilityRepository utilityRepository;

	public SuggestionController(
		final SuggestionRepository suggestionRepository,
		final UtilityRepository utilityRepository
	) {
		this.suggestionRepository = suggestionRepository;
		this.utilityRepository = utilityRepository;
	}

	@GetMapping("/v1/utility-payments/suggestions")
	public List<Suggestion> suggestions(@RequestHeader(value="x-mine-user-id") final UUID userId)
	{
		final List<Suggestion> suggestions = suggestionRepository.findFirst20ByUserAndStatusAndExpiredGreaterThanOrderByCreatedAsc(
				userId, Suggestion.ENABLED, OffsetDateTime.now());
		suggestions.forEach(s -> {
			s.setUtility(utilityRepository.findById(s.getUtilityId()).get());
		});
		return suggestions;
	}

	@DeleteMapping("/v1/utility-payments/suggestions/{id}")
	public ResponseEntity<Suggestion> delete(
		@RequestHeader(value="x-mine-user-id") final UUID userId,
		@PathVariable("id") final Long id
	) {
		final Optional<Suggestion> opt = suggestionRepository.findByUserAndId(userId, id);
		if (opt.isPresent() && Suggestion.ENABLED.equals(opt.get().getStatus())) {
			final Suggestion suggestion = opt.get();
			suggestion.setStatus(Suggestion.DISABLED);
			suggestionRepository.save(suggestion);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.of(Optional.empty());
	}
}
