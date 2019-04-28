package cl.multipay.utility.payments.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.object.dto.WebpayResultResponse;
import cl.tenpo.utility.payments.util.http.WebpayClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UtilityPaymentWebpayServiceTests
{
	@MockBean
	private CloseableHttpClient client;

	@Autowired
	private WebpayClient webpayClient;

	@Test
	public void result_shouldReturnEmpty() throws Exception
	{
		final Webpay utilityPaymentWebpay = new Webpay();
		utilityPaymentWebpay.setToken("ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e3");
		final String responseEntity = "{}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR));
		final Optional<WebpayResultResponse> result = webpayClient.result(utilityPaymentWebpay);
		Assert.notNull(result, "resut must not be null");
		Assert.isTrue(!result.isPresent(), "result.isPresent() must be false");
	}

	@Test
	public void result_shouldReturnNotEmpty() throws Exception
	{
		final Webpay utilityPaymentWebpay = new Webpay();
		utilityPaymentWebpay.setToken("ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e3");
		final String responseEntity = "{\"accountingDate\": \"0211\",\"buyOrder\": \"20190211144611003\",\"cardDetail\": {\"cardNumber\": \"3123\",\"cardExpirationDate\": null},\"detailOutput\": [{\"sharesAmount\": null,\"sharesNumber\": 0,\"amount\": 94290,\"commerceCode\": \"597020000540\",\"buyOrder\": \"20190211144611003\",\"authorizationCode\": \"000000\",\"paymentTypeCode\": \"VD\",\"responseCode\": -1}],\"sessionId\": \"fe166a72ee594a40bf232c1deee78c79\",\"transactionDate\": \"2019-02-11T14:46:47\",\"urlRedirection\": \"http://localhost:7771/v1/payments/webpay/final\",\"vci\": \"TSN\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		final Optional<WebpayResultResponse> result = webpayClient.result(utilityPaymentWebpay);
		Assert.notNull(result, "resut must not be null");
		Assert.isTrue(result.isPresent(), "result.isPresent() must be true");
	}

	@Test
	public void ack_shouldReturnOk() throws Exception
	{
		final Webpay utilityPaymentWebpay = new Webpay();
		utilityPaymentWebpay.setToken("ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e3");
		final String responseEntity = "{\"status\":true}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		final Optional<Boolean> result = webpayClient.ack(utilityPaymentWebpay);
		Assert.notNull(result, "resut must not be null");
		Assert.isTrue(result.isPresent(), "result.isPresent() must be true");
		Assert.isTrue(result.get(), "result must be true");
	}
}
