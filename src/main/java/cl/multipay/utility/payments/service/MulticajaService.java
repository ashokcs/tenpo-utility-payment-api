package cl.multipay.utility.payments.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
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

import cl.multipay.utility.payments.dto.Collector;
import cl.multipay.utility.payments.dto.MulticajaBill;
import cl.multipay.utility.payments.dto.MulticajaInitPayResponse;
import cl.multipay.utility.payments.dto.Utility;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.util.Properties;

@Service
public class MulticajaService
{
	private static final Logger logger = LoggerFactory.getLogger(MulticajaService.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public MulticajaService(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<List<Utility>> getUtilities()
	{
		try {
			final String url = properties.getMulticajaUtilitiesUrl();
			final HttpGet request = new HttpGet(url);
			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {}", url);
				logger.info("<= {}: {}", url, response.getStatusLine());

				if (response.getStatusLine().getStatusCode() == 200) {

					final ObjectMapper mapper = new ObjectMapper();
					final JsonNode utilitiesJsonNode = mapper.readTree(body);

					final JsonNode data = utilitiesJsonNode.get("data");
					final JsonNode utilities = data.get("convenios");
					if (utilities.isArray()) {
						final List<Utility> utilitiesList = new ArrayList<>();
						for (final JsonNode utility : utilities) {

							// utility name
							final String utilityName = utility.get("firm").asText();
							final Utility tmp = new Utility();
							tmp.setUtility(utilityName);

							// utility identifiers
							final JsonNode utilityIdentifiers = utility.get("gloss");
							final List<String> utilityIdentifiersList = new ArrayList<>();
							for (final Iterator<String> keys = utilityIdentifiers.fieldNames(); keys.hasNext();){
								final String key = keys.next();
								utilityIdentifiersList.add(utilityIdentifiers.get(key).asText());
							}
							tmp.setIdentifiers(utilityIdentifiersList);

							// utility collector
							final JsonNode collectors = utility.get("collector");
							for (final Iterator<String> keys = collectors.fieldNames(); keys.hasNext();){
								final String key = keys.next();
								final Collector collector = new Collector();
								collector.setId(key);
								collector.setName(collectors.get(key).asText());
								tmp.setCollector(collector);
								break;
							}

							// add to list
							utilitiesList.add(tmp);
						}
						return Optional.of(utilitiesList);
					}
				} else {
					logger.error("<= {}: {}", url, body);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<MulticajaBill> getBill(final String utility, final String collector)
	{
		try {
			final String url = properties.getMulticajaUtlitiesBillUrl();

			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode jsonObject = mapper.createObjectNode();
			jsonObject.put("terminal", properties.getMulticajaUtilitiesTerminal());
			jsonObject.put("commerce_id", properties.getMulticajaUtilitiesCommerce());
			jsonObject.put("firm", utility);
			jsonObject.put("collector", collector);
			jsonObject.put("payment_id", "1");
			final String json = mapper.writeValueAsString(jsonObject);

			final HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} [{}]", url, json);
				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final JsonNode billJsonNode = mapper.readTree(body);
					final JsonNode dataJsonNode = billJsonNode.get("data");
					final JsonNode debtsJsonNode = dataJsonNode.get("debts");
					for(final JsonNode bill : debtsJsonNode) {
						final MulticajaBill mcBill = new MulticajaBill();
						mcBill.setTransactionId(dataJsonNode.get("codigo_mc").asText());
						mcBill.setAmount(bill.get("monto_total").asLong());
						mcBill.setDueDate(bill.get("fecha_vencimiento").asText());
						return Optional.of(mcBill);
					}
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<MulticajaInitPayResponse> initPay(final Bill bill)
	{
		try {
			final String url = properties.getMulticajaPaymentUrl();
			final String apiKey = properties.getMulticajaPaymentApiKey();
			final String json = initPayJson(bill);

			final HttpPost request = new HttpPost(url);
			request.setHeader("apikey", apiKey);
			request.setHeader("Content-Type", "application/json");
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			try (final CloseableHttpResponse response = client.execute(request)) {

				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} [{}]", url, json);
				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 201) {
					final ObjectMapper mapper = new ObjectMapper();
					final JsonNode createOrderJson = mapper.readTree(body);
					final MulticajaInitPayResponse initPay = new MulticajaInitPayResponse();
					initPay.setOrderId(createOrderJson.get("order_id").asLong());
					initPay.setReferenceId(createOrderJson.get("reference_id").asText());
					initPay.setStatus(createOrderJson.get("status").asText());
					initPay.setRedirectUrl(createOrderJson.get("redirect_url").asText());
					return Optional.of(initPay);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private String initPayJson(final Bill bill)
		throws JsonProcessingException
	{
		final ObjectMapper mapper = new ObjectMapper();
		final ObjectNode request = mapper.createObjectNode();
		request.put("reference_id", bill.getPublicId());
		request.put("description", properties.getMulticajaPaymentDescription());

		if (bill.getEmail() != null ) {
			final ObjectNode user = mapper.createObjectNode();
			user.put("email", bill.getEmail());
			request.set("user", user);
		}

		final ObjectNode amount = mapper.createObjectNode();
		amount.put("currency", properties.getMulticajaPaymentCurrency());
		amount.put("total", bill.getAmount());
		request.set("amount", amount);

		final ArrayNode methods = mapper.createArrayNode();
		methods.add("tarjetas");
		request.set("methods", methods);

		final ObjectNode urls = mapper.createObjectNode();
		urls.put("return_url", properties.getMulticajaPaymentReturnUrl());
		urls.put("cancel_url", properties.getMulticajaPaymentCancelUrl());
		request.set("urls", urls);

		final ObjectNode webhooks = mapper.createObjectNode();
		webhooks.put("webhook_confirm", properties.getMulticajaPaymentConfirmUrl());
		request.set("webhook", webhooks);

		return mapper.writeValueAsString(request);
	}
}
