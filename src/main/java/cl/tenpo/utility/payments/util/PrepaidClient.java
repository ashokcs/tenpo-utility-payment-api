package cl.tenpo.utility.payments.util;

import java.util.Optional;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.tenpo.utility.payments.object.Balance;
import cl.tenpo.utility.payments.object.BalanceResponse;

@Component
public class PrepaidClient
{
	private static final Logger logger = LoggerFactory.getLogger(PrepaidClient.class);

	private final Properties properties;
	private final CloseableHttpClient client;

	public PrepaidClient(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public BalanceResponse balance(final UUID userId)
	{
		final BalanceResponse res = new BalanceResponse();
		try {
			final String url = properties.prepaidBalanceUrl.replaceAll("\\{user_id\\}", userId.toString());
			final HttpGet request = new HttpGet(url);

			logger.trace("=> {}", request.getRequestLine());

			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.trace("<= {} {}", response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final ObjectMapper mapper = new ObjectMapper();
					final JsonNode jsonNode = mapper.readTree(body);
					final Integer cc = jsonNode.get("balance").get("currency_code").asInt();
					final Long val = jsonNode.get("balance").get("value").asLong();
					final boolean up = jsonNode.get("updated").asBoolean();
					res.setBalance(Optional.of(new Balance(cc, val, up)));
				} else {
					logger.error("=> {}", request.getRequestLine());
					logger.error("<= {} {}", response.getStatusLine(), body);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return res;
	}
}
