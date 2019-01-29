package cl.multipay.utility.payments.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpClientConfig
{
	@Bean
	public CloseableHttpClient closeableHttpClient()
	{
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        final HttpHost proxy = new HttpHost("10.150.16.2", 9090, "http"); // TODO rm??

        final RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setProxy(proxy)
                .build();

		final HttpClientBuilder builder = HttpClients.custom();
		builder.disableAutomaticRetries();
		builder.disableConnectionState();
		builder.setConnectionManager(cm);
		builder.setDefaultRequestConfig(defaultRequestConfig);
		return builder.build();
	}
}
