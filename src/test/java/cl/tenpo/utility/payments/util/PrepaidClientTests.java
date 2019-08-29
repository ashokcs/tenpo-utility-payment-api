package cl.tenpo.utility.payments.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.UUID;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import cl.tenpo.utility.payments.mock.CloseableHttpResponseMock;
import cl.tenpo.utility.payments.object.BalanceResponse;
import io.nats.streaming.StreamingConnection;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class PrepaidClientTests
{
	@MockBean
	private StreamingConnection streamingConnection;

	@MockBean
	private CloseableHttpClient client;

	@Autowired
	private PrepaidClient prepaidClient;

	@Test
	public void balance_shouldBeNotEmpty() throws Exception
	{
		final String payload = "{\"balance\": {\"currency_code\":\"CLP\",\"value\":1234},\"updated\":false}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(payload, HttpStatus.OK));
		final BalanceResponse balance = prepaidClient.balance(UUID.randomUUID());
		assertThat(balance).isNotNull();
		assertThat(balance.getBalance()).isNotEmpty();
		assertThat(balance.getBalance().get().getBalance().getValue()).isEqualTo(1234);
	}

	@Test
	public void balance_shouldBeEmpty_whenBadRequest() throws Exception
	{
		final String payload = "{\"balance\": {\"currency_code\":\"CLP\",\"value\":1234},\"updated\":false}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(payload, HttpStatus.BAD_REQUEST));
		final BalanceResponse balance = prepaidClient.balance(UUID.randomUUID());
		assertThat(balance).isNotNull();
		assertThat(balance.getBalance()).isEmpty();
	}

	@Test
	public void balance_shouldBeEmpty_whenException() throws Exception
	{
		when(client.execute(any())).thenThrow(new IOException());
		final BalanceResponse balance = prepaidClient.balance(UUID.randomUUID());
		assertThat(balance).isNotNull();
		assertThat(balance.getBalance()).isEmpty();
	}
}
