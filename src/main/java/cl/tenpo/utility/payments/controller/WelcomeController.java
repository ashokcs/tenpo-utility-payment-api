package cl.tenpo.utility.payments.controller;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.entity.Welcome;
import cl.tenpo.utility.payments.repository.WelcomeRepository;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class WelcomeController
{
	private final WelcomeRepository welcomeRepository;

	public WelcomeController(final WelcomeRepository welcomeRepository)
	{
		this.welcomeRepository = welcomeRepository;
	}

	@PostMapping("/v1/utility-payments/welcome")
	public ResponseEntity<?> welcome(@RequestHeader(value="x-mine-user-id") final UUID userId)
	{
		if (welcomeRepository.findByUser(userId).isPresent() == false) {
			final Welcome welcome = new Welcome();
			welcome.setUser(userId);
			welcome.setCreated(OffsetDateTime.now());
			welcomeRepository.save(welcome);
			return ResponseEntity.status(HttpStatus.CREATED).build();
		} else {
			return ResponseEntity.ok().build();
		}
	}
}
