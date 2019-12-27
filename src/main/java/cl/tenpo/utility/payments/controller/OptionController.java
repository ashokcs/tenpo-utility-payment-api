package cl.tenpo.utility.payments.controller;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.entity.Option;
import cl.tenpo.utility.payments.object.OptionsRequest;
import cl.tenpo.utility.payments.repository.OptionRepository;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OptionController
{
	private final OptionRepository optionRepository;

	public OptionController(final OptionRepository optionRepository)
	{
		this.optionRepository = optionRepository;
	}

	@GetMapping("/v1/utility-payments/options")
	public ResponseEntity<?> options(@RequestHeader(value="x-mine-user-id") final UUID userId)
	{
		final Optional<Option> optional = optionRepository.findByUser(userId);
		if (optional.isPresent()) {
			return ResponseEntity.ok(optional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/v1/utility-payments/options")
	public ResponseEntity<?> optionsUpdate(
		@RequestHeader(value="x-mine-user-id") final UUID userId,
		@RequestBody @Valid final OptionsRequest request)
	{
		final Optional<Option> optional = optionRepository.findByUser(userId);
		if (optional.isPresent()) {
			final Option tmp = optional.get();
			tmp.setRemind(request.getRemind());
			tmp.setSuggest(request.getSuggest());
			tmp.setRemindFrequency(request.getRemindFrequency());
			optionRepository.save(tmp);
			return ResponseEntity.ok(tmp);
		} else {
			final Option tmp = new Option();
			tmp.setUser(userId);
			tmp.setRemind(request.getRemind());
			tmp.setSuggest(request.getSuggest());
			tmp.setRemindFrequency(request.getRemindFrequency());
			tmp.setCreated(OffsetDateTime.now());
			optionRepository.save(tmp);
			return ResponseEntity.ok(tmp);
		}
	}
}
