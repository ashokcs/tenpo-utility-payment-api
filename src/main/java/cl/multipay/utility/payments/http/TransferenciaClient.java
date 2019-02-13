package cl.multipay.utility.payments.http;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import cl.multipay.utility.payments.dto.TefCreateOrderResponse;
import cl.multipay.utility.payments.dto.TefGetOrderStatusResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.TransferenciaPayment;
import cl.multipay.utility.payments.util.Properties;

@Service
public class TransferenciaClient
{
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaClient.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public TransferenciaClient(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<TefCreateOrderResponse> createOrder(final Bill bill, final String tefPublicId, final String tefNotifyId)
	{
		try {
			final String url = properties.getTransferenciaCreateOrderUrl();
			final String authBase64 = properties.getTransferenciaBasicAuth();
			final String commerceId = properties.getTransferenciaCommerceId();
			final String branchId = properties.getTransferenciaBranchId();
			final String description = properties.getTransferenciaCreateOrderDescription();
			final int requestDuration = properties.getTransferenciaCreateOrderRequestDuration();
			final String goBackUrl = properties.getTransferenciaCreateOrderGoBackUrl().replaceAll("\\{id\\}", tefPublicId);
			final String notifyUrl = properties.getTransferenciaCreateOrderNotifyUrl().replaceAll("\\{id\\}", tefPublicId).replaceAll("\\{notify\\}", tefNotifyId);
			final String xml = createOrderXmlBody(bill, commerceId, branchId, description, requestDuration, goBackUrl, notifyUrl);

			final HttpPost post = new HttpPost(url);
			post.setHeader("Authorization", "Basic " + authBase64);
			post.setEntity(new StringEntity(xml, ContentType.TEXT_XML));

			try (final CloseableHttpResponse response = client.execute(post)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} [{}]", url, xml);
				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final Pattern mcOrderIdPattern = Pattern.compile("\\<ns2\\:mcOrderId\\>(\\d+)\\<\\/ns2\\:mcOrderId\\>");
					final Matcher mcOrderIdMatcher = mcOrderIdPattern.matcher(body);
					final Pattern redirectUrlPattern = Pattern.compile("\\<ns2\\:redirectUrl\\>(https\\:\\/\\/(.*)\\/bdp\\/order\\.xhtml\\?id\\=(\\d+))\\<\\/ns2\\:redirectUrl\\>");
					final Matcher redirectUrlMatcher = redirectUrlPattern.matcher(body);

					if (mcOrderIdMatcher.find() && redirectUrlMatcher.find()) {
						final TefCreateOrderResponse tefCreateOrderResponse = new TefCreateOrderResponse();
						tefCreateOrderResponse.setMcOrderId(mcOrderIdMatcher.group(1));
						tefCreateOrderResponse.setRedirectUrl(redirectUrlMatcher.group(1));
						return Optional.of(tefCreateOrderResponse);
					}
				}
			}

		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<TefGetOrderStatusResponse> getOrderStatus(final TransferenciaPayment transferenciaPayment)
	{
		try {
			final String url = properties.getTransferenciaGetOrderStatusUrl();
			final String authBase64 = properties.getTransferenciaBasicAuth();
			final String commerceId = properties.getTransferenciaCommerceId();
			final String branchId = properties.getTransferenciaBranchId();
			final String mcOrderId = transferenciaPayment.getMcOrderId();
			final String xml = getOrderStatusXml(commerceId, branchId, mcOrderId);

			final HttpPost post = new HttpPost(url);
			post.setHeader("Authorization", "Basic " + authBase64);
			post.setEntity(new StringEntity(xml, ContentType.TEXT_XML));

			try (final CloseableHttpResponse response = client.execute(post)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity);

				logger.info("=> {} [{}]", url, xml);
				logger.info("<= {}: {} [{}]", url, response.getStatusLine(), body);

				if (response.getStatusLine().getStatusCode() == 200) {
					final Pattern orderStatusPatter = Pattern.compile("\\<orderStatus\\>(\\d+)\\<\\/orderStatus\\>");
					final Matcher orderStatusMatcher = orderStatusPatter.matcher(body);
					final Pattern descriptionPattern = Pattern.compile("\\<description\\>(.*)\\<\\/description\\>");
					final Matcher descriptionMatcher = descriptionPattern.matcher(body);
					final Pattern ecOrderIdPattern = Pattern.compile("\\<ecOrderId\\>(.*)\\<\\/ecOrderId\\>");
					final Matcher ecOrderIdMatcher = ecOrderIdPattern.matcher(body);

					if (orderStatusMatcher.find() && ecOrderIdMatcher.find() && descriptionMatcher.find()) {
						final TefGetOrderStatusResponse tefGetOrderStatusResponse = new TefGetOrderStatusResponse();
						tefGetOrderStatusResponse.setOrderStatus(Integer.parseInt(orderStatusMatcher.group(1)));
						tefGetOrderStatusResponse.setDescription(descriptionMatcher.group(1));
						tefGetOrderStatusResponse.setEcoOrderId(ecOrderIdMatcher.group(1));
						return Optional.of(tefGetOrderStatusResponse);
					}
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	private String createOrderXmlBody(final Bill bill, final String commerceId,
		final String branchId, final String description, final int requestDuration,
		final String goBackUrl, final String notifyUrl)
	{
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" " +
			      "xmlns:cre=\"http://createorder.ws.boton.multicaja.cl/\">" +
			      "<soapenv:Header/>" +
			      "<soapenv:Body>" +
			         "<cre:createOrder>" +
			            "<cre:ecOrderId>"+bill.getBuyOrder()+"</cre:ecOrderId>" +
			            "<cre:commerceId>"+commerceId+"</cre:commerceId>" +
			            "<cre:branchId>"+branchId+"</cre:branchId>" +
			            "<cre:totalAmount>"+bill.getAmount()+"</cre:totalAmount>" +
			            "<cre:generalDescription>"+description+"</cre:generalDescription>" +
			            "<cre:requestDuration>"+requestDuration+"</cre:requestDuration>" +
			            "<cre:goBackUrl>"+goBackUrl+"</cre:goBackUrl>" +
			            "<cre:notificationUrl>"+notifyUrl+"</cre:notificationUrl>" +
			         "</cre:createOrder>" +
			      "</soapenv:Body>" +
			   "</soapenv:Envelope>";
	}

	private String getOrderStatusXml(final String commerceId, final String branchId, final String mcOrderId)
	{
		return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:get=\"http://getorderstatus.ws.boton.multicaja.cl/\">" +
				   "<soapenv:Header/>" +
				   "<soapenv:Body>" +
				      "<get:getOrderStatus>" +
				         "<get:commerceId>"+commerceId+"</get:commerceId>" +
				         "<get:branchId>"+branchId+"</get:branchId>" +
				         "<get:mcOrderId>"+mcOrderId+"</get:mcOrderId>" +
				      "</get:getOrderStatus>" +
				   "</soapenv:Body>" +
				"</soapenv:Envelope>";
	}
}
