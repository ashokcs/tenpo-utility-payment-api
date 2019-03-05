package cl.multipay.utility.payments.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.util.Properties;

@Service
public class RecaptchaClient
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentClient.class);

	private final CloseableHttpClient client;
	private final Properties properties;

	public RecaptchaClient(final CloseableHttpClient client, final Properties properties)
	{
		this.client = client;
		this.properties = properties;
	}

	public Optional<Boolean> check(final String recaptcha)
	{
		try {
			final String url = properties.recaptchaVerifyUrl;
			final HttpPost request = new HttpPost(url);
			final List<NameValuePair> nvps = new ArrayList <NameValuePair>();
			nvps.add(new BasicNameValuePair("secret", properties.recaptchaSecret));
			nvps.add(new BasicNameValuePair("response", recaptcha));
			request.setEntity(new UrlEncodedFormEntity(nvps));
			logger.info("=> {}", url);
			try (final CloseableHttpResponse response = client.execute(request)) {
				final HttpEntity entity = response.getEntity();
				final String body = EntityUtils.toString(entity).replaceAll("\\s", "");
				logger.info("<= {}: {} {}", url, response.getStatusLine(), body);
				if (response.getStatusLine().getStatusCode() == 200) {
					if (body.contains("\"success\":true")) {
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
