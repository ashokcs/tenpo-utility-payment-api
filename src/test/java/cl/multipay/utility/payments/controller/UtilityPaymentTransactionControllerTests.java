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

import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.TransferenciaService;
import cl.tenpo.utility.payments.service.WebpayService;
import cl.tenpo.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilityPaymentTransactionControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TransactionService utilityPaymentTransactionService;

	@Autowired
	private BillService utilityPaymentBillService;

	@Autowired
	private WebpayService utilityPaymentWebpayService;

	@Autowired
	private TransferenciaService utilityPaymentEftService;

	@Autowired
	private Utils utils;

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
		final Transaction upt = createUtilityPaymentTransactionMock();
		mockMvc.perform(get("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(upt.getPublicId())));
	}

	@Test
	public void getBill_shouldMethodNotAllowed() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionMock();
		mockMvc.perform(post("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void getBillWebpay_shouldReturnOk() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionMock();
		createUtilityPaymentWebpayMock(upt);
		mockMvc.perform(get("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(upt.getPublicId())));
	}

	@Test
	public void getBillEft_shouldReturnOk() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionMock();
		createUtilityPaymentEftMock(upt);
		mockMvc.perform(get("/v1/transactions/{id}", upt.getPublicId()).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(upt.getPublicId())));
	}

	@Test
	public void createBill_shouldReturnCreated() throws Exception
	{
		final String responseEntity = "{\"response_code\": 88,\"response_message\": \"APROBADA\",\"data\": {\"mc_code\": \"799480580\",\"authorization_code\": "
				+ "\"\",\"debts\": [{\"due_date\": \"22-11-2010\",\"payment_amount\": 1523,\"total_amount\": 1520,\"adjustment_amount\": -3,"
				+ "\"rsp_rut_client\": \"138046966\",\"rsp_name_client\": \"ALEXIS  RODRIGO CONTRERAS JARA\",\"rsp_invoice_number\": \"55605076\",\"confirmation\": "
				+ "\"SI\",\"authorizer\": \"EFT\",\"bill_payment_info\": \"Pago Cuentas Multicaja\",\"adjustment\": \"-3\",\"rsp_mc_code\": \"799480580\","
				+ "\"rsp_client_id\": \"19\",\"rsp_agreement_id\": \"1\",\"rsp_agreement_version_id\": \"1\",\"rsp_amount\": \"1523\",\"rsp_due_date\": \"22-11-2010\"}],\"debt_data_id\": 5}}";
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
				+ "\"No se encontro Deuda para este Numero de Documento\",\"data\":{\"mc_code\":\"799385026\"}}";
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
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\"}";
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
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void payBill_shouldReturnServerError1() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.NOT_FOUND));
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBill_shouldReturnServerError2() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.INTERNAL_SERVER_ERROR));
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBill_shouldReturnServerError3() throws Exception
	{
		when(client.execute(any())).thenReturn(null);
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/webpay", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBillTef_shouldReturnOk() throws Exception
	{
		final String responseEntity = "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\"><S:Body><ns2:createOrderResponse xmlns:ns2=\"http://createorder.ws.boton.multicaja.cl/\"><ns2:createOrderResult><ns2:mcOrderId>454439120367170</ns2:mcOrderId><ns2:redirectUrl>https://www.multicaja.cl/bdp/order.xhtml?id=454439120367170</ns2:redirectUrl></ns2:createOrderResult></ns2:createOrderResponse></S:Body></S:Envelope>";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/eft", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.url").isNotEmpty());
	}

	@Test
	public void payBillTef_shouldReturnServerError() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("", HttpStatus.UNSUPPORTED_MEDIA_TYPE));
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\"}";
		mockMvc.perform(post("/v1/transactions/{id}/eft", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void receipt_shouldReturnBadRequest_withNoData() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/transactions/{id}/receipt", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void receipt_shouldReturnBadRequest_withInvalidEmail() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"asd\"}";
		mockMvc.perform(post("/v1/transactions/{id}/receipt", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void receipt_shouldReturnBadRequest_withRecaptchaError() throws Exception
	{

		final Transaction upt = createUtilityPaymentTransactionMock();
		final String json = "{\"email\":\"test@multicaja.cl\", \"recaptcha\":\"asdasd\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.INTERNAL_SERVER_ERROR));
		mockMvc.perform(post("/v1/transactions/{id}/receipt", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void receiptEft_shouldReturnOk_withValidParams() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionSucceededMock();
		final String json = "{\"email\":\"test@multicaja.cl\", \"recaptcha\":\"asdasd\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{\"success\":true}", HttpStatus.OK));
		mockMvc.perform(post("/v1/transactions/{id}/receipt", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void receiptWebpay_shouldReturnOk_withValidParams() throws Exception
	{
		final Transaction upt = createUtilityPaymentTransactionSucceededWebpayMock();
		final String json = "{\"email\":\"test@multicaja.cl\", \"recaptcha\":\"asdasd\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{\"success\":true}", HttpStatus.OK));
		mockMvc.perform(post("/v1/transactions/{id}/receipt", upt.getPublicId()).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}

	private Transaction createUtilityPaymentTransactionMock()
	{
		final String uuid = utils.uuid();
		final Transaction utilityPaymentTransaction = new Transaction();
		utilityPaymentTransaction.setPublicId(uuid);
		utilityPaymentTransaction.setStatus(Transaction.PENDING);
		utilityPaymentTransaction.setAmount(1000L);
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction);

		final Bill utilityPaymentBill = new Bill();
		utilityPaymentBill.setStatus(Bill.PENDING);
		utilityPaymentBill.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentBill.setUtility("TEST");
		utilityPaymentBill.setCollectorId("2");
		utilityPaymentBill.setCategoryId("100");
		utilityPaymentBill.setIdentifier("123123");
		utilityPaymentBill.setMcCode1("12312312321");
		utilityPaymentBill.setAmount(123123L);
		utilityPaymentBill.setDueDate("2019-06-06");
		utilityPaymentBill.setMcCode1("123123123");
		utilityPaymentBillService.save(utilityPaymentBill);

		return utilityPaymentTransaction;
	}

	private Webpay createUtilityPaymentWebpayMock(final Transaction utilityPaymentTransaction)
	{
		final Webpay utilityPaymentWebpay = new Webpay();
		utilityPaymentWebpay.setStatus(Webpay.PENDING);
		utilityPaymentWebpay.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentWebpay.setToken("edec64843a14a828092719e4a9b2a243e3ed57577d42837b01fc7783e989e2e3");
		utilityPaymentWebpay.setUrl("https://webpay3gint.transbank.cl/webpayserver/initTransaction");
		utilityPaymentWebpayService.save(utilityPaymentWebpay);

		utilityPaymentTransaction.setStatus(Transaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(Transaction.WEBPAY);
		utilityPaymentTransaction.setEmail("test@multicaja.cl");
		utilityPaymentTransactionService.save(utilityPaymentTransaction);

		return utilityPaymentWebpay;
	}


	private Transferencia createUtilityPaymentEftMock(final Transaction utilityPaymentTransaction)
	{
		final Transferencia utilityPaymentEft = new Transferencia();
		utilityPaymentEft.setStatus(Transferencia.PENDING);
		utilityPaymentEft.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentEft.setPublicId("9ae45649f3ce4b868bc8169dfc92d4dd");
		utilityPaymentEft.setNotifyId("9ae45649f3ce4b868bc8169dfc92d4da");
		utilityPaymentEft.setOrder("853121364954859");
		utilityPaymentEft.setUrl("https://www.multicaja.cl/bdp/order.xhtml?id=853121364954859");
		utilityPaymentEftService.save(utilityPaymentEft);

		utilityPaymentTransaction.setStatus(Transaction.WAITING);
		utilityPaymentTransaction.setPaymentMethod(Transaction.TEF);
		utilityPaymentTransaction.setEmail("test@multicaja.cl");
		utilityPaymentTransactionService.save(utilityPaymentTransaction);

		return utilityPaymentEft;
	}

	private Transaction createUtilityPaymentTransactionSucceededMock()
	{
		final String uuid = utils.uuid();
		final Transaction utilityPaymentTransaction = new Transaction();
		utilityPaymentTransaction.setPublicId(uuid);
		utilityPaymentTransaction.setStatus(Transaction.SUCCEEDED);
		utilityPaymentTransaction.setAmount(1000L);
		utilityPaymentTransaction.setPaymentMethod(Transaction.TEF);
		utilityPaymentTransaction.setEmail("test@multicaja.cl");
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction);

		final Bill utilityPaymentBill = new Bill();
		utilityPaymentBill.setStatus(Bill.CONFIRMED);
		utilityPaymentBill.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentBill.setUtility("TEST");
		utilityPaymentBill.setCollectorId("2");
		utilityPaymentBill.setCategoryId("100");
		utilityPaymentBill.setIdentifier("123123");
		utilityPaymentBill.setMcCode1("12312312321");
		utilityPaymentBill.setAmount(123123L);
		utilityPaymentBill.setDueDate("2019-06-06");
		utilityPaymentBill.setMcCode1("123123123");
		utilityPaymentBillService.save(utilityPaymentBill);

		final Transferencia utilityPaymentEft = new Transferencia();
		utilityPaymentEft.setStatus(Transferencia.PAID);
		utilityPaymentEft.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentEft.setPublicId("9ae45649f3ce4b868bc8169dfc92d123");
		utilityPaymentEft.setNotifyId("9ae45649f3ce4b868bc8169dfc92d543");
		utilityPaymentEft.setOrder("853121364954859");
		utilityPaymentEft.setUrl("https://www.multicaja.cl/bdp/order.xhtml?id=853121364954859");
		utilityPaymentEftService.save(utilityPaymentEft);

		return utilityPaymentTransaction;
	}

	private Transaction createUtilityPaymentTransactionSucceededWebpayMock()
	{
		final String uuid = utils.uuid();
		final Transaction utilityPaymentTransaction = new Transaction();
		utilityPaymentTransaction.setPublicId(uuid);
		utilityPaymentTransaction.setStatus(Transaction.SUCCEEDED);
		utilityPaymentTransaction.setAmount(1000L);
		utilityPaymentTransaction.setPaymentMethod(Transaction.WEBPAY);
		utilityPaymentTransaction.setEmail("test@multicaja.cl");
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction);

		final Bill utilityPaymentBill = new Bill();
		utilityPaymentBill.setStatus(Bill.CONFIRMED);
		utilityPaymentBill.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentBill.setUtility("TEST");
		utilityPaymentBill.setCollectorId("2");
		utilityPaymentBill.setCategoryId("100");
		utilityPaymentBill.setIdentifier("123123");
		utilityPaymentBill.setMcCode1("12312312321");
		utilityPaymentBill.setAmount(123123L);
		utilityPaymentBill.setDueDate("2019-06-06");
		utilityPaymentBill.setMcCode1("123123123");
		utilityPaymentBillService.save(utilityPaymentBill);

		final Webpay utilityPaymentWebpay = new Webpay();
		utilityPaymentWebpay.setStatus(Webpay.ACKNOWLEDGED);
		utilityPaymentWebpay.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentWebpay.setToken("edec64843a14a828092719e4a9b2a243e3ed57577d42837b01fc7783e989e2e3");
		utilityPaymentWebpay.setUrl("https://webpay3gint.transbank.cl/webpayserver/initTransaction");
		utilityPaymentWebpayService.save(utilityPaymentWebpay);

		return utilityPaymentTransaction;
	}
}
