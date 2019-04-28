package cl.tenpo.utility.payments.util.http;

import java.math.BigDecimal;
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

import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.object.dto.WebpayInitResponse;
import cl.tenpo.utility.payments.object.dto.WebpayResultResponse;
import cl.tenpo.utility.payments.util.Properties;

@Component
public class WebpayClient
{
	private static final Logger logger = LoggerFactory.getLogger(WebpayClient.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public WebpayClient(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<WebpayInitResponse> init(final Transaction transaction, final String webpayPublicId)
	{
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode request = mapper.createObjectNode();
			request.put("amount", transaction.getAmount());
			request.put("sessionId", transaction.getPublicId());
			request.put("buyOrder", transaction.getOrder());
			request.put("returnUrl", properties.webpayReturnUrl.replaceAll("\\{id\\}", webpayPublicId));
			request.put("finalUrl", properties.webpayFinalUrl.replaceAll("\\{id\\}", webpayPublicId));
			final ObjectNode config = mapper.createObjectNode();
			config.put("commerceUserName", properties.webpayCommerceUser);
			config.put("commercePassword", properties.webpayCommercePass);
			config.put("commerceEnvironment", properties.webpayCommerceEnv);
			request.set("config", config);

			final String url = properties.webpayInitUrl;
			final String json = mapper.writeValueAsString(request);

			final HttpPost post = new HttpPost(url);
			post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			logger.info("=> {} [{}]", url, json);

			try (final CloseableHttpResponse response = client.execute(post)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final JsonNode jsonNode = mapper.readTree(body);
					final WebpayInitResponse webpayResponse = new WebpayInitResponse();
					webpayResponse.setUrl(jsonNode.get("url").asText());
					webpayResponse.setToken(jsonNode.get("token").asText());
					return Optional.of(webpayResponse);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<WebpayResultResponse> result(final Webpay webpay)
	{
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode request = mapper.createObjectNode();
			request.put("token", webpay.getToken());
			final ObjectNode config = mapper.createObjectNode();
			config.put("commerceUserName", properties.webpayCommerceUser);
			config.put("commercePassword", properties.webpayCommercePass);
			config.put("commerceEnvironment", properties.webpayCommerceEnv);
			request.set("config", config);

			final String url = properties.webpayResultUrl;
			final String json = mapper.writeValueAsString(request);

			final HttpPost post = new HttpPost(url);
			post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			logger.info("=> {} [{}]", url, json);

			try (final CloseableHttpResponse response = client.execute(post)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final JsonNode jsonNode = mapper.readTree(body);
					final WebpayResultResponse webpayResponse = new WebpayResultResponse();
					webpayResponse.setAccountingDate(jsonNode.get("accountingDate").asText());
					webpayResponse.setSessionId(jsonNode.get("sessionId").asText());
					webpayResponse.setTransactionDate(jsonNode.get("transactionDate").asText());
					webpayResponse.setBuyOrder(jsonNode.get("buyOrder").asText());
					webpayResponse.setVci(jsonNode.get("vci").asText());
					webpayResponse.setUrlRedirection(jsonNode.get("urlRedirection").asText());
					final JsonNode cardDetail = jsonNode.get("cardDetail");
					if ((cardDetail != null) && cardDetail.isObject()) {
						webpayResponse.setCardNumber(cardDetail.get("cardNumber").asText());
					}
					final JsonNode detailOutput = jsonNode.get("detailOutput");
					if ((detailOutput != null) && detailOutput.isArray()) {
						for (final JsonNode detailOutputNode : detailOutput) {
							webpayResponse.setDetailAmount(new BigDecimal(detailOutputNode.get("amount").asLong()));
							webpayResponse.setDetailAuthorizationCode(detailOutputNode.get("authorizationCode").asText());
							webpayResponse.setDetailSharesAmount(new BigDecimal(detailOutputNode.get("sharesAmount").asLong()));
							webpayResponse.setDetailSharesNumber(detailOutputNode.get("sharesNumber").asInt());
							webpayResponse.setDetailCommerceCode(detailOutputNode.get("commerceCode").asText());
							webpayResponse.setDetailPaymentTypeCode(detailOutputNode.get("paymentTypeCode").asText());
							webpayResponse.setDetailBuyOrder(detailOutputNode.get("buyOrder").asText());
							webpayResponse.setDetailResponseCode(detailOutputNode.get("responseCode").asInt(-1));
						}
					}
					return Optional.of(webpayResponse);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Boolean> ack(final Webpay webpay)
	{
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final ObjectNode request = mapper.createObjectNode();
			request.put("token", webpay.getToken());
			final ObjectNode config = mapper.createObjectNode();
			config.put("commerceUserName", properties.webpayCommerceUser);
			config.put("commercePassword", properties.webpayCommercePass);
			config.put("commerceEnvironment", properties.webpayCommerceEnv);
			request.set("config", config);

			final String url = properties.webpayAckUrl;
			final String json = mapper.writeValueAsString(request);

			final HttpPost post = new HttpPost(url);
			post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));

			logger.info("=> {} [{}]", url, json);

			try (final CloseableHttpResponse response = client.execute(post)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final JsonNode jsonNode = mapper.readTree(body);
					final boolean result = jsonNode.get("status").asBoolean(false);
					if (result) {
						return Optional.of(true);
					}
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
