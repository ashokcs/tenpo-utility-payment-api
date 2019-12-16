package cl.tenpo.utility.payments.controller;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import io.nats.streaming.StreamingConnection;

@RestController
public class NatsController
{
	private final StreamingConnection streamingConnection;

	public NatsController(final StreamingConnection streamingConnection)
	{
		this.streamingConnection = streamingConnection;
	}

	@PostMapping("/nats/publish")
	public void publish(
		@RequestHeader("subject") final String subject,
		@RequestBody final String body
	) throws IOException, InterruptedException, TimeoutException
	{
		streamingConnection.publish(subject, body.getBytes(), (a, b) -> {});
	}
}
