package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cl.multipay.utility.payments.dto.WebpayInitResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.util.Properties;

@Service
public class WebpayService
{
	private static final Logger logger = LoggerFactory.getLogger(WebpayService.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public WebpayService(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<WebpayInitResponse> init(final Bill bill)
	{
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode request = mapper.createObjectNode();
			request.put("amount", bill.getAmount());
			request.put("sessionId", "");
			request.put("buyOrder", 0L); // TODO
			request.put("returnUrl", properties.getWebpayReturnUrl());
			request.put("finalUrl", properties.getWebpayFinalUrl());
			final ObjectNode config = mapper.createObjectNode();
			config.put("commerceUserName", properties.getWebpayCommerceUser());
			config.put("commercePassword", properties.getWebpayCommercePass());
			config.put("commerceEnvironment", properties.getWebpayCommerceEnv());
			request.set("config", config);

			final String url = properties.getWebpayUrl();
			final String json = mapper.writeValueAsString(request);

			final HttpPost post = new HttpPost(url);
			post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			try (final CloseableHttpResponse response = client.execute(post)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} [{}]", url, json);
				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				final JsonNode jsonNode = mapper.readTree(body);
				final WebpayInitResponse webpayResponse = new WebpayInitResponse();
				webpayResponse.setUrl(jsonNode.get("url").asText());
				webpayResponse.setToken(jsonNode.get("token").asText());
				return Optional.of(webpayResponse);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
