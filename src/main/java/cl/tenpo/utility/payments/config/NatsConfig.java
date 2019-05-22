package cl.tenpo.utility.payments.config;

import java.io.IOException;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener.Events;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;

@Configuration
public class NatsConfig
{
	private static Logger logger = LoggerFactory.getLogger(NatsConfig.class);

	@Value("${nats.cluster.url}") private String clusterUrl;
	@Value("${nats.cluster.id}") private String clusterId;
	@Value("${nats.client.id}") private String clientId;

	@Bean
	public Connection nats()
		throws IOException, InterruptedException
	{
		logger.info("--- Cluster Url : {}", clusterUrl);
		logger.info("--- Cluster Id  : {}", clusterId);
		logger.info("--- Client Id   : {}", clientId);

		final Options o = new Options.Builder()
				.server(clusterUrl)
				.maxReconnects(-1)
				.reconnectWait(Duration.ofSeconds(10))
				.connectionListener(this::connectionEvent)
				.connectionName(clientId)
				.build();

		return Nats.connect(o);
	}

	@Bean
	public StreamingConnection stan(@Autowired final Connection nats)
		throws IOException, InterruptedException
	{
		final StreamingConnectionFactory cf = new StreamingConnectionFactory(clusterId, clientId);
		cf.setNatsConnection(nats);
		return cf.createConnection();
	}

	private void connectionEvent(final Connection conn, final Events type)
	{
		logger.info(type.toString());
	}
}
