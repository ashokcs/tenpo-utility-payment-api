package cl.multipay.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import cl.multipay.utility.payments.dto.WebpayResultResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.WebpayPayment;
import cl.multipay.utility.payments.http.WebpayClient;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.EmailService;
import cl.multipay.utility.payments.service.WebpayPaymentService;
import cl.multipay.utility.payments.util.Properties;
import cl.multipay.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class WebpayControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Properties properties;

	@Autowired
	private BillService billService;

	@Autowired
	private WebpayPaymentService webpayPaymentService;

	@MockBean
	private WebpayClient webpayClient;

	@MockBean
	private EmailService emailService;

	@Test
	public void webpayReturn_shouldReturnFound_withNoParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/return"))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(header().string("Location", properties.getWebpayRedirectError()));
	}

	@Test
	public void webpayReturn_shouldReturnFound_withInvalidParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", "65as4d5sad65asf46fddff"))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(header().string("Location", properties.getWebpayRedirectError()));
	}

	@Test
	public void webpayReturn_shouldReturnFound_withValidParameters() throws Exception
	{
		final Bill bill = createBillMock(Bill.WAITING);
		final WebpayPayment webpayPayment = createWebpayPaymentMock(bill, WebpayPayment.PENDING);
		when(webpayClient.result(any())).thenReturn(Optional.empty());
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", webpayPayment.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayReturn_shouldReturnOk_withValidParametersApproved() throws Exception
	{
		final Bill bill = createBillMock(Bill.WAITING);
		final WebpayPayment webpayPayment = createWebpayPaymentMock(bill, WebpayPayment.PENDING);
		when(webpayClient.result(any())).thenReturn(createWebpayResultResponseMock(bill, 0));
		when(webpayClient.ack(any())).thenReturn(Optional.of(true));
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", webpayPayment.getToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void webpayReturn_shouldReturnOk_withValidParametersDenied() throws Exception
	{
		final Bill bill = createBillMock(Bill.WAITING);
		final WebpayPayment webpayPayment = createWebpayPaymentMock(bill, WebpayPayment.PENDING);
		when(webpayClient.result(any())).thenReturn(createWebpayResultResponseMock(bill, -1));
		when(webpayClient.ack(any())).thenReturn(Optional.of(true));
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", webpayPayment.getToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withInvalidParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/final"))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTokenWsSucced() throws Exception
	{
		final Bill bill = createBillMock(Bill.SUCCEED);
		final WebpayPayment webpayPayment = createWebpayPaymentMock(bill, WebpayPayment.ACK);
		mockMvc.perform(post("/v1/payments/webpay/final").param("token_ws", webpayPayment.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTokenWsFailed() throws Exception
	{
		final Bill bill = createBillMock(Bill.FAILED);
		final WebpayPayment webpayPayment = createWebpayPaymentMock(bill, WebpayPayment.ACK);
		mockMvc.perform(post("/v1/payments/webpay/final").param("token_ws", webpayPayment.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTbkToken() throws Exception
	{
		final Bill bill = createBillMock(Bill.FAILED);
		final WebpayPayment webpayPayment = createWebpayPaymentMock(bill, WebpayPayment.ACK);
		mockMvc.perform(post("/v1/payments/webpay/final").param("TBK_TOKEN", webpayPayment.getToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTbkBuyOrder() throws Exception
	{
		final Bill bill = createBillMock(Bill.FAILED);
		mockMvc.perform(post("/v1/payments/webpay/final").param("TBK_ORDEN_COMPRA", bill.getBuyOrder().toString()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	private Bill createBillMock(final String status)
	{
		final String uuid = Utils.uuid();
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
		bill.setBuyOrder("PC201902112113000001");
		billService.save(bill);
		return bill;
	}

	private WebpayPayment createWebpayPaymentMock(final Bill bill, final String status)
	{
		final WebpayPayment webpay = new WebpayPayment();
		webpay.setBillId(bill.getId());
		webpay.setStatus(status);
		webpay.setToken("ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e3");
		webpay.setUrl("https://webpay3gint.transbank.cl/webpayserver/initTransaction");
		webpayPaymentService.save(webpay);
		return webpay;
	}

	private Optional<WebpayResultResponse> createWebpayResultResponseMock(final Bill bill, final int code)
	{
		final WebpayResultResponse webpayResponse = new WebpayResultResponse();
		webpayResponse.setAccountingDate("0211");
		webpayResponse.setSessionId("fe166a72ee594a40bf232c1deee78c79");
		webpayResponse.setTransactionDate("2019-02-11T14:46:47");
		webpayResponse.setBuyOrder(bill.getBuyOrder().toString());
		webpayResponse.setVci("TSN");
		webpayResponse.setUrlRedirection("http://localhost:7771/v1/payments/webpay/final");
		webpayResponse.setCardNumber("3123");
		webpayResponse.setDetailAmount(new BigDecimal(bill.getAmount()));
		webpayResponse.setDetailAuthorizationCode("1231231");
		webpayResponse.setDetailSharesAmount(new BigDecimal(1000));
		webpayResponse.setDetailSharesNumber(0);
		webpayResponse.setDetailCommerceCode("597020000540");
		webpayResponse.setDetailPaymentTypeCode("VD");
		webpayResponse.setDetailBuyOrder(bill.getBuyOrder().toString());
		webpayResponse.setDetailResponseCode(code);
		return Optional.of(webpayResponse);
	}
}
