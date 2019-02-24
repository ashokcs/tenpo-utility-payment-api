package cl.multipay.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentEft;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.multipay.utility.payments.service.UtilityPaymentBillService;
import cl.multipay.utility.payments.service.UtilityPaymentEftService;
import cl.multipay.utility.payments.service.UtilityPaymentTransactionService;
import cl.multipay.utility.payments.util.Properties;
import cl.multipay.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("unused")
public class UtilityPaymentEftControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityPaymentEftService utilityPaymentEftService;

	@Autowired
	private UtilityPaymentTransactionService utilityPaymentTransactionService;

	@Autowired
	private UtilityPaymentBillService utilityPaymentBillService;

	@Autowired
	private Properties properties;

	@MockBean
	private CloseableHttpClient client;

	@Test
	public void tefReturn_shouldReturnNotFound_withInvalidParameters1() throws Exception
	{
		mockMvc.perform(get("/v1/payments/eft/return/{id}", "123"))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withInvalidParameters2() throws Exception
	{
		mockMvc.perform(get("/v1/payments/eft/return/{id}", "738b0aa174544c4a92e511b904ed32a4"))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withRemoteCanceled() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a1";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d1";
		final String mcOrderId = "526415205094961";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>105</orderStatus><description>CANCELLED</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/eft/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withRemoteError() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a2";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d2";
		final String mcOrderId = "526415205094962";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/eft/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withRemotePaid() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a3";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d3";
		final String mcOrderId = "526415205094963";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>101</orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/eft/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withBillPending() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a4";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d4";
		final String mcOrderId = "526415205094963";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>101</orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/eft/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidVerb() throws Exception
	{
		mockMvc.perform(get("/v1/payments/eft/notify/{id}/{notifyId}", "738b0aa174544c4a92e511b904ed32a4", "738b0aa174544c4a92e511b904ed32a4"))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withNoContentType() throws Exception
	{
		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", "738b0aa174544c4a92e511b904ed32a4", "738b0aa174544c4a92e511b904ed32a4"))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withNoAuthHeader() throws Exception
	{
		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", "738b0aa174544c4a92e511b904ed32a4", "738b0aa174544c4a92e511b904ed32a4").contentType(MediaType.TEXT_XML))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidParamenters() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a5";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d5";
		final String mcOrderId = "526415205094964";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", "738b0aa174544c4a92e511b904ed32a5", "5e0a1eeace5d4708b74c5e9029bae3d5")
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic asdasdasd"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidParamenters1() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a6";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d6";
		final String mcOrderId = "526415205094964";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", tefPublicId, "5e0a1eeace5d4708b74c5e9029bae3dd")
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic asdasdasd"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidAuthHeader() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a7";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d7";
		final String mcOrderId = "526415205094964";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic asdasdasd"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withValidPendingBill() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a8";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d8";
		final String mcOrderId = "526415205094964";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic " + properties.eftNotifyBasicAuth))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnOk_withParamenters() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a9";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d9";
		final String mcOrderId = "526415205094965";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>101</orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic " + properties.eftNotifyBasicAuth))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withErrorRemote() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionlMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32f1";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3f1";
		final String mcOrderId = "526415205094966";
		final UtilityPaymentEft utilityPaymentEft = createUtilityPaymentEftMock(utilityPaymentTransaction, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>100/orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(post("/v1/payments/eft/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic " + properties.eftNotifyBasicAuth))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	private UtilityPaymentTransaction createUtilityPaymentTransactionlMock(final String status, final String uuid, final Long buyOrder)
	{
		final UtilityPaymentTransaction utilityPaymentTransaction = new UtilityPaymentTransaction();
		utilityPaymentTransaction.setPublicId(uuid);
		utilityPaymentTransaction.setStatus(status);
		utilityPaymentTransaction.setAmount(1000L);
		utilityPaymentTransaction.setEmail("test@multicaja.cl");
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction);
		utilityPaymentTransaction.setBuyOrder(buyOrder);
		utilityPaymentTransactionService.save(utilityPaymentTransaction);

		final UtilityPaymentBill utilityPaymentBill = new UtilityPaymentBill();
		utilityPaymentBill.setStatus(UtilityPaymentBill.PENDING);
		utilityPaymentBill.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentBill.setUtility("TEST");
		utilityPaymentBill.setCollector("2");
		utilityPaymentBill.setCategory("100");
		utilityPaymentBill.setIdentifier("123123");
		utilityPaymentBill.setMcCode("12312312321");
		utilityPaymentBill.setAmount(123123L);
		utilityPaymentBill.setDueDate("2019-06-06");
		utilityPaymentBill.setMcCode("123123123");
		utilityPaymentBillService.save(utilityPaymentBill);

		return utilityPaymentTransaction;
	}

	private UtilityPaymentEft createUtilityPaymentEftMock(final UtilityPaymentTransaction utilityPaymentTransaction, final String tefPublicId,
		final String tefNotifyId, final String mcOrderId)
	{
		final UtilityPaymentEft utilityPaymentEft = new UtilityPaymentEft();
		utilityPaymentEft.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentEft.setStatus(UtilityPaymentEft.PENDING);
		utilityPaymentEft.setPublicId(tefPublicId);
		utilityPaymentEft.setNotifyId(tefNotifyId);
		utilityPaymentEft.setOrder(mcOrderId);
		utilityPaymentEft.setUrl("http://bla.bla");
		utilityPaymentEftService.save(utilityPaymentEft);
		return utilityPaymentEft;
	}
}
