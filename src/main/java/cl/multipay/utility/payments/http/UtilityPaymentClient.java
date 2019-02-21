package cl.multipay.utility.payments.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import cl.multipay.utility.payments.dto.Collector;
import cl.multipay.utility.payments.dto.MulticajaBill;
import cl.multipay.utility.payments.dto.Utility;
import cl.multipay.utility.payments.util.Properties;

@Component
public class UtilityPaymentClient
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentClient.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public UtilityPaymentClient(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<List<Utility>> getUtilities()
	{
		try {
			final String url = properties.getMulticajaUtilitiesUrl();

			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode jsonObject = mapper.createObjectNode();
			jsonObject.put("terminal", properties.getMulticajaUtilitiesTerminal());
			jsonObject.put("channel", properties.getMulticajaUtilitiesChannel());
			final String json = mapper.writeValueAsString(jsonObject);

			final HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("apikey", properties.getMulticajaUtilitiesApiKey());
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			logger.info("=> {}", url);

			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("<= {}: {}", url, response.getStatusLine());

				if (response.getStatusLine().getStatusCode() == 200) {

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

	public Optional<MulticajaBill> getBill(final String utility, final String identifier, final String collector)
	{
		try {
			final String url = properties.getMulticajaUtlitiesBillUrl();

			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode jsonObject = mapper.createObjectNode();
			jsonObject.put("terminal", properties.getMulticajaUtilitiesTerminal());
			jsonObject.put("commerce_id", properties.getMulticajaUtilitiesCommerce());
			jsonObject.put("firm", utility);
			jsonObject.put("collector", collector);
			jsonObject.put("payment_id", identifier);
			final String json = mapper.writeValueAsString(jsonObject);

			final HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("apikey", properties.getMulticajaUtilitiesApiKey());
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			logger.info("=> {} [{}]", url, json);

			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final JsonNode billJsonNode = mapper.readTree(body);
					final Integer responseCode = billJsonNode.get("response_code").asInt(99);
					final String responseMessage = billJsonNode.get("response_message").asText("ERROR");
					if (responseCode.equals(88) && responseMessage.equals("APROBADA")) {
						final JsonNode dataJsonNode = billJsonNode.get("data");
						final JsonNode debtsJsonNode = dataJsonNode.get("debts");
						for(final JsonNode bill : debtsJsonNode) {
							final MulticajaBill mcBill = new MulticajaBill();
							mcBill.setMcCode(dataJsonNode.get("codigo_mc").asText());
							mcBill.setAmount(bill.get("monto_total").asLong());
							mcBill.setDueDate(bill.get("fecha_vencimiento").asText());
							return Optional.of(mcBill);
						}
					}
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
