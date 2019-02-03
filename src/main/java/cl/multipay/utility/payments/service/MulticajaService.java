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

import cl.multipay.utility.payments.dto.MulticajaInitPayResponse;
import cl.multipay.utility.payments.entity.Bill;

@Service
public class MulticajaService
{
	private static final Logger logger = LoggerFactory.getLogger(MulticajaService.class);

	private final CloseableHttpClient client;

	public MulticajaService(final CloseableHttpClient client)
	{
		this.client = client;
	}

	public Optional<MulticajaInitPayResponse> initPay(final Bill bill)
	{
		try {
			// TODO properties
			final String url = "https://api.staging.multicajadigital.cloud/payment-gateway/v1/orders";
			final String apiKey = "mKaTZ4yBm3rVFapqNctziKCvXsjD6fDO";
			final String json = createOrderJson(bill);

			final HttpPost request = new HttpPost(url);
			request.setHeader("apikey", apiKey);
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			try (final CloseableHttpResponse response = client.execute(request)) {

				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				if (response.getStatusLine().getStatusCode() == 201) {
					final ObjectMapper mapper = new ObjectMapper();
					final JsonNode createOrderJson = mapper.readTree(body);
					final MulticajaInitPayResponse initPay = new MulticajaInitPayResponse();
					initPay.setOrderId(createOrderJson.get("order_id").asLong());
					initPay.setReferenceId(createOrderJson.get("reference_id").asText());
					initPay.setStatus(createOrderJson.get("status").asText());
					initPay.setRedirectUrl(createOrderJson.get("redirect_url").asText());
					return Optional.ofNullable(initPay);
				} else {
					logger.error("[{}] [{}] : [{}] [{}]", url, json, response.getStatusLine().toString(), body);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private String createOrderJson(final Bill bill)
		throws JsonProcessingException
	{
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode request = mapper.createObjectNode();
		request.put("reference_id", bill.getPublicId());
		request.put("description", "Pago en Multipay.cl"); // TODO properties

		if (bill.getEmail() != null ) {
			final ObjectNode user = mapper.createObjectNode();
			user.put("email", bill.getEmail());
			request.set("user", user);
		}

		final ObjectNode amount = mapper.createObjectNode();
		amount.put("currency", "CLP"); // TODO properties
		amount.put("total", bill.getAmount());
		request.set("amount", amount);

		final ArrayNode methods = mapper.createArrayNode();
		methods.add("tarjetas");
		request.set("methods", methods);

		final ObjectNode urls = mapper.createObjectNode();
		urls.put("return_url", "http://localhost/return"); // TODO properties
		urls.put("cancel_url", "http://localhost/cancel"); // TODO properties
		request.set("urls", urls);

		final ObjectNode webhooks = mapper.createObjectNode();
		webhooks.put("webhook_confirm", "http://localhost/confirm"); // TODO properties
		request.set("webhook", webhooks);

		return mapper.writeValueAsString(request);
	}
}
