package cl.multipay.utility.payments.config;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.ssl.SSLContextBuilder;
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
	public CloseableHttpClient closeableHttpClient(final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager)
	{
        final Builder configBuilder = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.DEFAULT)
                .setExpectContinueEnabled(true)
                .setSocketTimeout(15000)
                .setConnectTimeout(15000)
                .setConnectionRequestTimeout(15000);
        final RequestConfig defaultRequestConfig = configBuilder.build();

		final HttpClientBuilder builder = HttpClients.custom();
		builder.disableAutomaticRetries();
		builder.disableConnectionState();
		builder.setConnectionManager(poolingHttpClientConnectionManager);
		builder.setDefaultRequestConfig(defaultRequestConfig);

		if ((properties.httpClientProxy != null) && !properties.httpClientProxy.isEmpty()) {
			try {
				final URI proxyUrl = new URI(properties.httpClientProxy);
				final HttpHost proxy = new HttpHost(proxyUrl.getHost(), proxyUrl.getPort(), proxyUrl.getScheme());
				final HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy) {
					@Override
				    public HttpRoute determineRoute(
				            final HttpHost host,
				            final HttpRequest request,
				            final HttpContext context) throws HttpException
					{
						if ((properties.httpClientProxyExclude != null) && !properties.httpClientProxyExclude.isEmpty()) {
							if (properties.httpClientProxyExclude.contains(host.getHostName())) {
								return new HttpRoute(host);
							}
						}
						return super.determineRoute(host, request, context);
				    }
				};
				builder.setRoutePlanner(routePlanner);
			} catch (final URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
        }

		return builder.build();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager()
	{
		final RegistryBuilder<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory())
				.register("https", SSLConnectionSocketFactory.getSocketFactory());

		if (properties.httpClientTrustAll) {
            try {
                final SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
                sslContextBuilder.loadTrustMaterial(new TrustSelfSignedStrategy());
                final SSLContext sslContext = sslContextBuilder.build();
                final SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
                registry.register("https", sslSocketFactory);
            } catch (final Exception e) {
            	logger.error(e.getMessage(), e);
            }
        }

		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(registry.build());
        cm.setMaxTotal(100);
        return cm;
	}
}
