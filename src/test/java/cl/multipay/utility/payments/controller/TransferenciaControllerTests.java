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

import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.TransferenciaPayment;
import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.EmailService;
import cl.multipay.utility.payments.service.TransferenciaPaymentService;
import cl.multipay.utility.payments.util.Properties;
import cl.multipay.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("unused")
public class TransferenciaControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransferenciaPaymentService transferenciaPaymentService;

	@Autowired
	private BillService billService;

	@Autowired
	private Properties properties;

	@MockBean
	private CloseableHttpClient client;

	@MockBean
	private EmailService emailService;

	@Test
	public void tefReturn_shouldReturnServerError_withInvalidParameters1() throws Exception
	{
		mockMvc.perform(get("/v1/payments/transferencia/return/asdasd"))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withInvalidParameters2() throws Exception
	{
		mockMvc.perform(get("/v1/payments/transferencia/return/738b0aa174544c4a92e511b904ed32a4"))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withRemoteCanceled() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a1";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d1";
		final String mcOrderId = "526415205094961";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>105</orderStatus><description>CANCELLED</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/transferencia/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withRemoteError() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a2";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d2";
		final String mcOrderId = "526415205094962";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/transferencia/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withRemotePaid() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a3";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d3";
		final String mcOrderId = "526415205094963";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>101</orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/transferencia/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefReturn_shouldReturnServerError_withBillPending() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a3";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d3";
		final String mcOrderId = "526415205094963";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>101</orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/payments/transferencia/return/{id}", tefPublicId))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidVerb() throws Exception
	{
		mockMvc.perform(get("/v1/payments/transferencia/notify/{id}/{notifyId}", "asd", "asd"))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withNoContentType() throws Exception
	{
		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", "asd", "asd"))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withNoAuthHeader() throws Exception
	{
		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", "asd", "asd").contentType(MediaType.TEXT_XML))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidParamenters() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a4";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d4";
		final String mcOrderId = "526415205094964";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", "asd", "asd")
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic asdasdasd"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidParamenters1() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a4";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d4";
		final String mcOrderId = "526415205094964";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", tefPublicId, "asd")
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic asdasdasd"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withInvalidAuthHeader() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a4";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d4";
		final String mcOrderId = "526415205094964";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic asdasdasd"))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withValidPendingBill() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.PENDING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a4";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d4";
		final String mcOrderId = "526415205094964";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic " + properties.getTransferenciaNotifyBasicAuth()))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void tefNotify_shouldReturnOk_withParamenters() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a5";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d5";
		final String mcOrderId = "526415205094965";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>101</orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic " + properties.getTransferenciaNotifyBasicAuth()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void tefNotify_shouldReturnServerError_withErrorRemote() throws Exception
	{
		final String uuid = Utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Bill bill = createBillMock(Bill.WAITING, uuid, buyOrder);
		final String tefPublicId = "738b0aa174544c4a92e511b904ed32a6";
		final String tefNotifyId = "5e0a1eeace5d4708b74c5e9029bae3d6";
		final String mcOrderId = "526415205094966";
		final TransferenciaPayment payment = createTransferenciaPaymentMock(bill, tefPublicId, tefNotifyId, mcOrderId);

		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><getOrderStatusResponse xmlns=\"http://getorderstatus.ws.boton.multicaja.cl/\"><getOrderStatusResult><orderStatus>100/orderStatus><description>PAID</description><ecOrderId>1201902141550020021</ecOrderId></getOrderStatusResult></getOrderStatusResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(post("/v1/payments/transferencia/notify/{id}/{notifyId}", tefPublicId, tefNotifyId)
				.contentType(MediaType.TEXT_XML).header("Authorization", "Basic " + properties.getTransferenciaNotifyBasicAuth()))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	private Bill createBillMock(final String status, final String uuid, final Long buyOrder)
	{
		final Bill bill = new Bill();
		bill.setPublicId(uuid);
		bill.setStatus(status);
		bill.setUtility("TEST");
		bill.setCollector("2");
		bill.setIdentifier("123123");
		bill.setAmount(123123L);
		bill.setDueDate("2019-06-06");
		bill.setTransactionId("123123123");
		bill.setEmail("asd@asd.cl");
		bill.setPayment(Bill.WEBPAY);
		billService.save(bill);
		bill.setBuyOrder(buyOrder);
		billService.save(bill);
		return bill;
	}

	private TransferenciaPayment createTransferenciaPaymentMock(final Bill bill, final String tefPublicId,
		final String tefNotifyId, final String mcOrderId)
	{
		final TransferenciaPayment transferenciaPayment = new TransferenciaPayment();
		transferenciaPayment.setBillId(bill.getId());
		transferenciaPayment.setStatus(TransferenciaPayment.PENDING);
		transferenciaPayment.setPublicId(tefPublicId);
		transferenciaPayment.setNotifyId(tefNotifyId);
		transferenciaPayment.setMcOrderId(mcOrderId);
		transferenciaPayment.setUrl("http://bla.bla");
		transferenciaPaymentService.save(transferenciaPayment);
		return transferenciaPayment;
	}
}
