package cl.tenpo.utility.payments.config;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
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
	public CloseableHttpClient closeableHttpClient(final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager)
	{
        final Builder configBuilder = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(25000)
                .setConnectTimeout(25000)
                .setConnectionRequestTimeout(25000);
        final RequestConfig defaultRequestConfig = configBuilder.build();

		final HttpClientBuilder builder = HttpClients.custom();
		builder.disableAutomaticRetries();
		builder.disableConnectionState();
		builder.setConnectionManager(poolingHttpClientConnectionManager);
		builder.setDefaultRequestConfig(defaultRequestConfig);
		builder.setKeepAliveStrategy((a,b) -> {return 60 * 1000;});

		return builder.build();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager()
	{
		final RegistryBuilder<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSocketFactory());

		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry.build());
        cm.setMaxTotal(100);
        return cm;
	}
}
