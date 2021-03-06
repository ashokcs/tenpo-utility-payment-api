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
import cl.tenpo.utility.payments.object.UtilityBillResponse;

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

	public UtilityBillResponse getBills(final String utility, final String collector, final String identifier)
	{
		// set default response
		final List<UtilityBillItem> bills = new ArrayList<>();
		final UtilityBillResponse response = new UtilityBillResponse();
		response.setBills(bills);
		response.setUnavailable(false);
		response.setRetry(false);

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

			try (final CloseableHttpResponse res = client.execute(request)) {
				final HttpEntity entity = res.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} {}", request.getRequestLine(), json);
				logger.info("<= {} {}", res.getStatusLine(), body);

				if (res.getStatusLine().getStatusCode() == 200) {
					final JsonNode billJsonNode = mapper.readTree(body);
					final Integer responseCode = billJsonNode.get("response_code").asInt(99);
					final String responseMessage = billJsonNode.get("response_message").asText("ERROR");

					if (responseCode.equals(1) && (responseMessage.equals("Datos de deuda obtenidos exitosamente"))) {
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
							if (tmp.getAmount().compareTo(0L) > 0) {
								bills.add(tmp);
								number++;
							}
						}
						if (bills.size() == 2) {
							if (bills.get(0).getDesc().equals(bills.get(1).getDesc())) {
								if (bills.get(0).getAmount().compareTo(bills.get(1).getAmount()) >= 0) {
									bills.get(0).setDesc("Deuda total");
								} else {
									bills.get(1).setDesc("Deuda total");
								}
							}
						}
						if (bills.size() == 1) {
							bills.get(0).setDesc("Deuda por pagar");
						}
					} else if (responseCode.equals(79) && (responseMessage.equals("REINTENTO..."))) {
						final JsonNode dataJsonNode = billJsonNode.get("data");
						final String retryCollector = dataJsonNode.get("retry_").asText();
						response.setRetry(true);
						response.setRetryCollector(retryCollector);
					} else if (containsKnownErrorMessage(responseCode, responseMessage)) {
						logger.error("Unavailable: true " +
								"MULTICAJA PDC API: " + responseMessage + " (" + utility + ")");
						NewRelic.noticeError("MULTICAJA PDC API: " + responseMessage + " (" + utility + ")");
						response.setUnavailable(true);
					}
				} else {
					logger.error("Unavailable: true " +
							"MULTICAJA PDC API: " + res.getStatusLine() + " (" + utility + ")");
					response.setUnavailable(true);
					NewRelic.noticeError("MULTICAJA PDC API: " + res.getStatusLine() + " (" + utility + ")");
				}
			}
		} catch (final Exception e) {
			response.setUnavailable(true);
			NewRelic.noticeError(e);
			logger.error("Unavailable: true ");
			logger.error(e.getMessage(), e);
		}
		return response;
	}

	private boolean containsKnownErrorMessage(final Integer responseCode, final String responseMessage)
	{
		return !responseCode.equals(1) && (
				responseMessage.contains("RESPUESTA NO VALIDA") ||
				responseMessage.contains("ERROR ACCESO TABLA") ||
				responseMessage.contains("ERROR EJECUTAR SERVICIO INTERNO") ||
				responseMessage.contains("Error al intentar conectar con autorizador") ||
				responseMessage.contains("Error General") ||
				responseMessage.contains("El interlocutor") ||
				responseMessage.contains("REINTENTO")
			   );
	}
}
