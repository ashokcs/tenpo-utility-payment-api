package cl.multipay.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.impl.client.CloseableHttpClient;
import org.hamcrest.Matchers;
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
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;
import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.multipay.utility.payments.service.UtilityPaymentBillService;
import cl.multipay.utility.payments.service.UtilityPaymentEftService;
import cl.multipay.utility.payments.service.UtilityPaymentTransactionService;
import cl.multipay.utility.payments.service.UtilityPaymentWebpayService;
import cl.multipay.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilityPaymentTransactionControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UtilityPaymentTransactionService utilityPaymentTransactionService;

	@Autowired
	private UtilityPaymentBillService utilityPaymentBillService;

	@Autowired
	private UtilityPaymentWebpayService utilityPaymentWebpayService;

	@Autowired
	private UtilityPaymentEftService utilityPaymentEftService;

	@MockBean
	private CloseableHttpClient client;

	@Test
	public void getBill_shouldReturnUnsupportedMediaType() throws Exception
	{
		mockMvc.perform(get("/v1/transactions/6c2bba7b4aff418bb576eb180a2b1ea5").contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void getBill_shouldReturnNotFound() throws Exception
	{
		mockMvc.perform(get("/v1/transactions/6c2bba7b4aff418bb576eb180a2b1ea5").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	public void getBill_shouldReturnOk() throws Exception
	{
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		mockMvc.perform(get("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(upt.getPublicId())));
	}

	@Test
	public void getBill_shouldMethodNotAllowed() throws Exception
	{
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		mockMvc.perform(post("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void getBillWebpay_shouldReturnOk() throws Exception
	{
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		createUtilityPaymentWebpayMock(upt);
		mockMvc.perform(get("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(upt.getPublicId())));
	}

	@Test
	public void getBillEft_shouldReturnOk() throws Exception
	{
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		createUtilityPaymentEftMock(upt);
		mockMvc.perform(get("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(upt.getPublicId())));
	}

	@Test
	public void createBill_shouldReturnCreated() throws Exception
	{
		final String responseEntity = "{\"response_code\":88,\"response_message\":\"APROBADA\",\"data\":{\"codigo_mc\":\"799378736\","
				+ "\"authorization_code\":\"\",\"debts\":[{\"fecha_vencimiento\":\"2015-02-23\",\"monto_pago\":94295,\"monto_total\":94290"
				+ ",\"monto_ajuste\":-5,\"convenio\":\"9129\",\"confirmacion\":\"SI\",\"ajuste\":\"-5\",\"cargo_servicio\":\"0\"}]}}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		final String json = "{\"utility\": \"ENTEL\", \"collector\":\"2\", \"category\":\"100\", \"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/transactions").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNotEmpty());
	}

	@Test
	public void createBill_shouldReturnNoContent() throws Exception
	{
		final String responseEntity = "{\"response_code\":99,\"response_message\":"
				+ "\"No se encontro Deuda para este Numero de Documento\",\"data\":{\"codigo_mc\":\"799385026\"}}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		final String json = "{\"utility\": \"ENTEL\", \"collector\":\"2\", \"category\":\"100\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/transactions").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNoContent());
	}

	@Test
	public void createBill_shouldReturnBadRequest1() throws Exception
	{
		final String json = "{,,}";
		mockMvc.perform(post("/v1/transactions").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBill_shouldReturnBadRequest2() throws Exception
	{
		final String json = "{\"utility_id\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/transactions").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBill_shouldReturnBadRequest3() throws Exception
	{
		final String json = "{\"utility\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/transactions").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBill_shouldReturnUnsupportedMediaType() throws Exception
	{
		final String json = "{\"utility\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/transactions").content(json).contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void createBill_shouldReturnMethodNotAllowed() throws Exception
	{
		final String json = "{\"utility\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(get("/v1/transactions").content(json).contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void payBill_shouldReturnOk() throws Exception
	{
		final String responseEntity = "{\"url\":\"https:\\\\bla.com\",\"token\":\"askdhaksjhdkashdj\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@test.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.url").isNotEmpty())
			.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	public void payBill_shouldReturnBadRequest() throws Exception
	{
		final String responseEntity = "{\"url\":\"https:\\\\bla.com\",\"token\":\"askdhaksjhdkashdj\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void payBill_shouldReturnServerError1() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.NOT_FOUND));
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@test.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBill_shouldReturnServerError2() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.INTERNAL_SERVER_ERROR));
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@test.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBill_shouldReturnServerError3() throws Exception
	{
		when(client.execute(any())).thenReturn(null);
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@test.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBillTef_shouldReturnOk() throws Exception
	{
		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createOrderResponse xmlns:ns2=\"http://createorder.ws.boton.multicaja.cl/\"><ns2:createOrderResult><ns2:mcOrderId>454439120367170</ns2:mcOrderId><ns2:redirectUrl>https://www.multicaja.cl/bdp/order.xhtml?id=454439120367170</ns2:redirectUrl></ns2:createOrderResult></ns2:createOrderResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@test.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/eft", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.url").isNotEmpty());
	}

	@Test
	public void payBillTef_shouldReturnServerError() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("", HttpStatus.UNSUPPORTED_MEDIA_TYPE));
		final UtilityPaymentTransaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@test.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/eft", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	private UtilityPaymentTransaction createUtilityPaymentTransactionMock()
	{
		final String uuid = Utils.uuid();
		final UtilityPaymentTransaction utilityPaymentTransaction = new UtilityPaymentTransaction();
		utilityPaymentTransaction.setPublicId(uuid);
		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.PENDING);
		utilityPaymentTransaction.setAmount(1000L);
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction);

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

	private UtilityPaymentWebpay createUtilityPaymentWebpayMock(final UtilityPaymentTransaction utilityPaymentTransaction)
	{
		final UtilityPaymentWebpay utilityPaymentWebpay = new UtilityPaymentWebpay();
		utilityPaymentWebpay.setStatus(UtilityPaymentWebpay.PENDING);
		utilityPaymentWebpay.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentWebpay.setToken("edec64843a14a828092719e4a9b2a243e3ed57577d42837b01fc7783e989e2e3");
		utilityPaymentWebpay.setUrl("https://webpay3gint.transbank.cl/webpayserver/initTransaction");
		utilityPaymentWebpayService.save(utilityPaymentWebpay);

		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(UtilityPaymentTransaction.WEBPAY);
		utilityPaymentTransaction.setEmail("asd@asd.cl");
		utilityPaymentTransactionService.save(utilityPaymentTransaction);

		return utilityPaymentWebpay;
	}


	private UtilityPaymentEft createUtilityPaymentEftMock(final UtilityPaymentTransaction utilityPaymentTransaction)
	{
		final UtilityPaymentEft utilityPaymentEft = new UtilityPaymentEft();
		utilityPaymentEft.setStatus(UtilityPaymentEft.PENDING);
		utilityPaymentEft.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentEft.setPublicId("9ae45649f3ce4b868bc8169dfc92d4dd");
		utilityPaymentEft.setNotifyId("9ae45649f3ce4b868bc8169dfc92d4da");
		utilityPaymentEft.setOrder("853121364954859");
		utilityPaymentEft.setUrl("https://www.multicaja.cl/bdp/order.xhtml?id=853121364954859");
		utilityPaymentEftService.save(utilityPaymentEft);

		utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(UtilityPaymentTransaction.EFT);
		utilityPaymentTransaction.setEmail("asd@asd.cl");
		utilityPaymentTransactionService.save(utilityPaymentTransaction);

		return utilityPaymentEft;
	}
}
