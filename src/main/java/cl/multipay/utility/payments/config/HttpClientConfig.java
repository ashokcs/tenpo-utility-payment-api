package cl.multipay.utility.payments.config;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cl.multipay.utility.payments.util.Properties;

@Configuration
public class HttpClientConfig
{
	private static final Logger logger = LoggerFactory.getLogger(HttpClientConfig.class);

	private final Properties properties;

	public HttpClientConfig(final Properties properties)
	{
		this.properties = properties;
	}

	@Bean
	public CloseableHttpClient closeableHttpClient()
	{
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        final Builder configBuilder = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000);

        if ((properties.getHttpClientProxy() != null) && !properties.getHttpClientProxy().isEmpty()) {
			try {
				final URI proxyUrl = new URI(properties.getHttpClientProxy());
				final HttpHost proxy = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getScheme());
				configBuilder.setProxy(proxy);
			} catch (final URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
        }

        final RequestConfig defaultRequestConfig = configBuilder.build();

		final HttpClientBuilder builder = HttpClients.custom();
		builder.disableAutomaticRetries();
		builder.disableConnectionState();
		builder.setConnectionManager(cm);
		builder.setDefaultRequestConfig(defaultRequestConfig);
		return builder.build();
	}
}
