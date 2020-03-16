package cl.tenpo.utility.payments.controller;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.jpa.entity.Welcome;
import cl.tenpo.utility.payments.jpa.repository.WelcomeRepository;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class WelcomeController
{
	private final WelcomeRepository welcomeRepository;

	public WelcomeController(final WelcomeRepository welcomeRepository)
	{
		this.welcomeRepository = welcomeRepository;
	}

	@Deprecated
	@PostMapping("/v1/utility-payments/welcome")
	public ResponseEntity<?> welcome(@RequestHeader(value="x-mine-user-id") final UUID userId)
	{
		final Optional<Welcome> opt = welcomeRepository.findByUser(userId);
		if (opt.isPresent()) {
			final Welcome welcome = opt.get();
			welcome.setVisits(welcome.getVisits()+1);
			return ResponseEntity.ok(welcomeRepository.save(welcome));
		} else {
			final Welcome welcome = new Welcome();
			welcome.setUser(userId);
			welcome.setCreated(OffsetDateTime.now());
			welcome.setVisits(1);
			welcome.setTos(0);
			return ResponseEntity.ok(welcomeRepository.save(welcome));
		}
	}

	@PostMapping("/v1/utility-payments/welcome/tos")
	public ResponseEntity<?> tos(@RequestHeader(value="x-mine-user-id") final UUID userId)
	{
		final Optional<Welcome> opt = welcomeRepository.findByUser(userId);
		if (opt.isPresent()) {
			final Welcome welcome = opt.get();
			welcome.setTos(1);
			welcomeRepository.save(welcome);
			return ResponseEntity.ok(welcome);
		}
		return ResponseEntity.notFound().build();
	}
}
