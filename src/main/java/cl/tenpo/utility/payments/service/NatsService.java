package cl.tenpo.utility.payments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.nats.streaming.StreamingConnection;

@Service
public class NatsService
{
	private static Logger logger = LoggerFactory.getLogger(NatsService.class);

	private final StreamingConnection streamingConnection;

	public NatsService(final StreamingConnection streamingConnection)
	{
		this.streamingConnection = streamingConnection;
	}

	public void publish(final String subject, final byte[] data)
	{
		try {
			streamingConnection.publish(subject, data, this::onAck);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void onAck(final String nuid, final Exception ex)
	{
		if (ex != null) {
			logger.error("Error publishing msg id {}: {}", nuid, ex.getMessage());
        }
	}
}
