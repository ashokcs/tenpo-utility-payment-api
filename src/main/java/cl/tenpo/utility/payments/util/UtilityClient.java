package cl.tenpo.utility.payments.util;

import java.util.ArrayList;
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
import com.newrelic.api.agent.NewRelic;

import cl.tenpo.utility.payments.object.UtilityBillItem;

@Component
public class UtilityClient
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityClient.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public UtilityClient(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<String> getUtilities()
	{
		try {
			final String url = properties.multicajaUtilitiesUrl;
			final HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("apikey", properties.multicajaUtilitiesApiKey);
			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.trace("=> {}", request.getRequestLine());
				logger.trace("<= {}", response.getStatusLine());

				if (response.getStatusLine().getStatusCode() == 200) {
					return Optional.of(body);
				} else {
					logger.error("=> {}", request.getRequestLine());
					logger.error("<= {} {}", response.getStatusLine(), body);
				}
			}
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public List<UtilityBillItem> getBills(final String utility, final String collector, final String identifier)
	{
		final List<UtilityBillItem> bills = new ArrayList<>();
		try {
			final String url = properties.multicajaUtlitiesDebtUrl;

			// create json
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode jsonObject = mapper.createObjectNode();
			jsonObject.put("firm", utility);
			jsonObject.put("collector", collector);
			jsonObject.put("payment_id", identifier);
			final String json = mapper.writeValueAsString(jsonObject);

			// create request
			final HttpPost request = new HttpPost(url);
			request.setHeader("Content-Type", "application/json");
			request.setHeader("apikey", properties.multicajaUtilitiesApiKey);
			request.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} {}", request.getRequestLine(), json);
				logger.info("<= {} {}", response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final JsonNode billJsonNode = mapper.readTree(body);
					final Integer responseCode = billJsonNode.get("response_code").asInt(99);
					final String responseMessage = billJsonNode.get("response_message").asText("ERROR");
					if (responseCode.equals(88) && (responseMessage.equals("APROBADA") || responseMessage.equals("SIN MENSAJE"))) {
						final JsonNode dataJsonNode = billJsonNode.get("data");
						final JsonNode debtsJsonNode = dataJsonNode.get("debts");
						int number = 1;
						for(final JsonNode bill : debtsJsonNode) {
							final UtilityBillItem tmp = new UtilityBillItem();
							tmp.setOrder(number);
							tmp.setMcCode(dataJsonNode.get("mc_code").asText());
							tmp.setDebtDataId(dataJsonNode.get("debt_data_id").asLong());
							tmp.setAmount(bill.get("payment_amount").asLong());
							tmp.setDueDate(bill.get("due_date").asText());
							if (bill.get("order") != null) {
								tmp.setOrder(bill.get("order").asInt(number));
							} else {
								tmp.setOrder(number);
							}
							tmp.setDesc("Deuda vencida");
							if ("D.TOTAL".equals(tmp.getDueDate())) {
								tmp.setDesc("Deuda total");
								tmp.setDueDate("No disponible");
							}
							if ("D.VENCIDA".equals(tmp.getDueDate())) {
								tmp.setDesc("Deuda vencida");
								tmp.setDueDate("No disponible");
							}
							bills.add(tmp);
							number++;
						}
					}
				}
			}
		} catch (final Exception e) {
			NewRelic.noticeError(e);
			logger.error(e.getMessage(), e);
		}
		return bills;
	}
}
