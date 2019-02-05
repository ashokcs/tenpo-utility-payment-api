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

import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BillsControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BillService billService;

	@MockBean
	private CloseableHttpClient client;

	@Test
	public void getBill_shouldReturnUnsupportedMediaType() throws Exception
	{
		mockMvc.perform(get("/v1/bills/6c2bba7b4aff418bb576eb180a2b1ea5").contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void getBill_shouldReturnNotFound() throws Exception
	{
		mockMvc.perform(get("/v1/bills/6c2bba7b4aff418bb576eb180a2b1ea5").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	public void getBill_shouldReturnOk() throws Exception
	{
		final String uuid = createBillMock();
		mockMvc.perform(get("/v1/bills/{id}", uuid).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.bill_id", Matchers.equalTo(uuid)));
	}

	@Test
	public void getBill_shouldMethodNotAllowed() throws Exception
	{
		final String uuid = createBillMock();
		mockMvc.perform(post("/v1/bills/{id}", uuid).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void createBill_shouldReturnOk() throws Exception
	{
		final String responseEntity = "{\"response_code\":88,\"response_message\":\"APROBADA\",\"data\":{\"codigo_mc\":\"799378736\","
				+ "\"authorization_code\":\"\",\"debts\":[{\"fecha_vencimiento\":\"2015-02-23\",\"monto_pago\":94295,\"monto_total\":94290"
				+ ",\"monto_ajuste\":-5,\"convenio\":\"9129\",\"confirmacion\":\"SI\",\"ajuste\":\"-5\",\"cargo_servicio\":\"0\"}]}}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		final String json = "{\"utility\": \"ENTEL\", \"collector\":\"2\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/bills").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.bill_id").isNotEmpty());
	}

	@Test
	public void createBill_shouldReturnBadRequest1() throws Exception
	{
		final String json = "{,,}";
		mockMvc.perform(post("/v1/bills").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBill_shouldReturnBadRequest2() throws Exception
	{
		final String json = "{\"utility_id\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/bills").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBill_shouldReturnBadRequest3() throws Exception
	{
		final String json = "{\"utility\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/bills").content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isBadRequest());
	}

	@Test
	public void createBill_shouldReturnUnsupportedMediaType() throws Exception
	{
		final String json = "{\"utility\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/bills").content(json).contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void createBill_shouldReturnMethodNotAllowed() throws Exception
	{
		final String json = "{\"utility\": \"as\",\"identifier\": \"123\"}";
		mockMvc.perform(get("/v1/bills").content(json).contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void payBill_shouldReturnOk() throws Exception
	{
		final String responseEntity = "{\"order_id\":123,\"reference_id\":\"asd\",\"status\":\"pending\",\"redirect_url\":\"http:\\\\bla.com\"}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.CREATED));
		final String uuid = createBillMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/bills/{id}/pay", uuid).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.redirect_url").isNotEmpty());
	}

	@Test
	public void payBill_shouldReturnServerError1() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.NOT_FOUND));
		final String uuid = createBillMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/bills/{id}/pay", uuid).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBill_shouldReturnServerError2() throws Exception
	{
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock("{}", HttpStatus.INTERNAL_SERVER_ERROR));
		final String uuid = createBillMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/bills/{id}/pay", uuid).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	@Test
	public void payBill_shouldReturnServerError3() throws Exception
	{
		when(client.execute(any())).thenReturn(null);
		final String uuid = createBillMock();
		final String json = "{}";
		mockMvc.perform(post("/v1/bills/{id}/pay", uuid).content(json).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isInternalServerError());
	}

	private String createBillMock()
	{
		final String uuid = Utils.uuid();
		final Bill bill = new Bill();
		bill.setPublicId(uuid);
		bill.setStatus(Bill.STATUS_PENDING);
		bill.setUtility("TEST");
		bill.setCollector("2");
		bill.setIdentifier("123123");
		bill.setAmount(123123L);
		bill.setDueDate("2019-06-06");
		bill.setTransactionId("123123123");
		bill.setEmail("asd@asd.cl");
		billService.save(bill);
		return uuid;
	}
}
