package cl.tenpo.utility.payments.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.object.StatusResponse;
import cl.tenpo.utility.payments.service.NatsService;
import cl.tenpo.utility.payments.util.UtilityClient;

@RestController
public class HealthController
{
	private final NatsService natsService;
	private final UtilityClient utilityClient;

	public HealthController(
		final NatsService natsService,
		final UtilityClient utilityClient
	) {
		this.natsService = natsService;
		this.utilityClient = utilityClient;
	}

	@GetMapping("/v1/utility-payments/health/nats")
	public ResponseEntity<StatusResponse> nats()
	{
		if (natsService.isNatsConnected()) {
			return ResponseEntity.ok(new StatusResponse("UP"));
		} else {
			return ResponseEntity.status(500).body(new StatusResponse("DOWN"));
		}
	}

	@GetMapping("/v1/utility-payments/health/multicaja")
	public ResponseEntity<StatusResponse> multicaja()
	{
		if (utilityClient.getUtilities().isPresent()) {
			return ResponseEntity.ok(new StatusResponse("UP"));
		} else {
			return ResponseEntity.status(500).body(new StatusResponse("DOWN"));
		}
	}
}