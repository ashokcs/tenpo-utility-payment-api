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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cl.multipay.utility.payments.dto.MulticajaCreateOrderResponse;
import cl.multipay.utility.payments.entity.UtilityPaymentIntent;

@Service
public class MulticajaPaymentPlatformService
{
	private static final Logger logger = LoggerFactory.getLogger(MulticajaPaymentPlatformService.class);

	private final CloseableHttpClient client;

	public MulticajaPaymentPlatformService(final CloseableHttpClient client)
	{
		this.client = client;
	}

	public Optional<MulticajaCreateOrderResponse> initPay(final UtilityPaymentIntent utilityPaymentIntent)
	{
		try {
			// TODO properties
			final String url = "https://api.staging.multicajadigital.cloud/payment-gateway/v1/orders";
			final String apiKey = "mKaTZ4yBm3rVFapqNctziKCvXsjD6fDO";
			final String json = createOrderJson(utilityPaymentIntent);

			final HttpPost request = new HttpPost(url);
			request.setHeader("apikey", apiKey);
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			try (final CloseableHttpResponse response = client.execute(request)) {

				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info(">>> "+json);
				logger.info("<<< ["+response.getStatusLine().toString()+"] "+body);

				if (response.getStatusLine().getStatusCode() == 201) {
					final ObjectMapper mapper = new ObjectMapper();
					final JsonNode createOrderJson = mapper.readTree(body);
					final MulticajaCreateOrderResponse createOrderResponse = new MulticajaCreateOrderResponse();
					createOrderResponse.setOrderId(createOrderJson.get("order_id").asLong());
					createOrderResponse.setReferenceId(createOrderJson.get("reference_id").asText());
					createOrderResponse.setStatus(createOrderJson.get("status").asText());
					createOrderResponse.setRedirectUrl(createOrderJson.get("redirec_url").asText());
					return Optional.ofNullable(createOrderResponse);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private String createOrderJson(final UtilityPaymentIntent utilityPaymentIntent)
		throws JsonProcessingException
	{
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode request = mapper.createObjectNode();
		request.put("reference_id", utilityPaymentIntent.getOc());
		request.put("description", "Pago en Multipay.cl"); // TODO properties

		if (utilityPaymentIntent.getEmail() != null ) {
			final ObjectNode user = mapper.createObjectNode();
			user.put("email", utilityPaymentIntent.getEmail());
			request.set("user", user);
		}

		final ObjectNode amount = mapper.createObjectNode();
		amount.put("currency", "CLP"); // TODO properties
		amount.put("total", 1000); // TODO from db
		request.set("amount", amount);

		final ArrayNode methods = mapper.createArrayNode();
		methods.add("tarjetas");
		request.set("methods", methods);

		final ObjectNode urls = mapper.createObjectNode();
		urls.put("return_url", "http://localhost/return"); // TODO properties
		urls.put("cancel_url", "http://localhost/cancel"); // TODO properties
		request.set("urls", urls);

		final ObjectNode webhooks = mapper.createObjectNode();
		webhooks.put("webhook_confirm", "http://localhost/cancel2"); // TODO properties
		request.set("webhook", webhooks);

		return mapper.writeValueAsString(request);
	}
}
