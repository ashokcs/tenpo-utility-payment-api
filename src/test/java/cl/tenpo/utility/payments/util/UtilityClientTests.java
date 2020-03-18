package cl.tenpo.utility.payments.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
import cl.tenpo.utility.payments.object.UtilityBillItem;
import cl.tenpo.utility.payments.object.UtilityBillResponse;
import io.nats.streaming.StreamingConnection;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilityClientTests
{
	@MockBean
	private StreamingConnection streamingConnection;

	@MockBean
	private CloseableHttpClient client;

	@Autowired
	private UtilityClient utilityClient;

	@Test
	public void getUtilities_shouldBeNotEmpty() throws Exception
	{
		final String payload = "{}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(payload, HttpStatus.OK));
		final Optional<String> utilities = utilityClient.getUtilities();
		assertThat(utilities).isNotNull();
		assertThat(utilities).isNotEmpty();
	}

	@Test
	public void getUtilities_shouldBeEmpty() throws Exception
	{
		final String payload = "{}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(payload, HttpStatus.INTERNAL_SERVER_ERROR));
		final Optional<String> utilities = utilityClient.getUtilities();
		assertThat(utilities).isNotNull();
		assertThat(utilities).isEmpty();
	}


	@Test
	public void getUtilities_shouldBeEmpty_whenException() throws Exception
	{
		when(client.execute(any())).thenThrow(new IOException());
		final Optional<String> utilities = utilityClient.getUtilities();
		assertThat(utilities).isNotNull();
		assertThat(utilities).isEmpty();
	}

	@Test
	public void getBills_shouldBeNotEmpty() throws Exception
	{
		final String payload = "{" +
				"  \"response_code\": 88," +
				"  \"response_message\": \"APROBADA\"," +
				"  \"data\": {" +
				"    \"mc_code\": \"1570079266\"," +
				"    \"debts\": [" +
				"      {" +
				"        \"order\": \"1\"," +
				"        \"due_date\": \"D.TOTAL\"," +
				"        \"payment_amount\": 12571," +
				"        \"total_amount\": 12570," +
				"        \"adjustment_amount\": -1" +
				"      }," +
				"      {" +
				"        \"order\": \"2\"," +
				"        \"due_date\": \"D.VENCIDA\"," +
				"        \"payment_amount\": 12571," +
				"        \"total_amount\": 12570," +
				"        \"adjustment_amount\": -1" +
				"      }" +
				"    ]," +
				"    \"debt_data_id\": 17" +
				"  }" +
				"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(payload, HttpStatus.OK));
		final UtilityBillResponse response = utilityClient.getBills("utility", "collector", "ASD123");
		final List<UtilityBillItem> bills = response.getBills();

		assertThat(bills).isNotNull();
		assertThat(bills).isNotEmpty();
		assertThat(bills.size()).isEqualTo(2);
	}

	@Test
	public void getBills_shouldBeEmpty_whenBadError() throws Exception
	{
		final String payload = "{}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(payload, HttpStatus.INTERNAL_SERVER_ERROR));
		final UtilityBillResponse response = utilityClient.getBills("utility", "collector", "ASD123");
		final List<UtilityBillItem> bills = response.getBills();
		assertThat(bills).isNotNull();
		assertThat(bills).isEmpty();
	}
}
