package cl.multipay.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import org.springframework.test.web.servlet.MockMvc;

import cl.multipay.utility.payments.mock.CloseableHttpResponseMock;
import cl.tenpo.utility.payments.dto.WebpayResultResponse;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.WebpayService;
import cl.tenpo.utility.payments.util.Properties;
import cl.tenpo.utility.payments.util.Utils;
import cl.tenpo.utility.payments.util.http.WebpayClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilityPaymentWebpayControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private Properties properties;

	@Autowired
	private TransactionService utilityPaymentTransactionService;

	@Autowired
	private BillService utilityPaymentBillService;

	@Autowired
	private WebpayService utilityPaymentWebpayService;

	@Autowired
	private Utils utils;

	@MockBean
	private WebpayClient webpayClient;

	@MockBean
	private CloseableHttpClient client;

	@Test
	public void webpayReturn_shouldReturnFound_withNoParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/return"))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(header().string("Location", properties.webpayFrontError));
	}

	@Test
	public void webpayReturn_shouldReturnFound_withInvalidParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", "65as4d5sad65asf46fddff"))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(header().string("Location", properties.webpayFrontError));
	}

	@Test
	public void webpayReturn_shouldReturnFound_withValidParameters() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.WAITING, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e1";
		final Webpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, Webpay.PENDING, token);
		when(webpayClient.result(any())).thenReturn(Optional.empty());
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayReturn_shouldReturnOk_withValidParametersApproved() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000002L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.WAITING, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e2";
		final Webpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, Webpay.PENDING, token);

		final String responseEntity = "{\"response_code\": 1,\"response_message\": \"APROBADA\",\"data\": {\"authorization_code\": \"1231313\",\"date\": \"06/03/2019\",\"hour\": \"09:25:46\",\"mc_code\": \"799483722\",\"confirm_payment_id\":123,\"tx_state_iso\":\"APROBADO\"}}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));
		when(webpayClient.result(any())).thenReturn(createWebpayResultResponseMock(utilityPaymentTransaction, 0));
		when(webpayClient.ack(any())).thenReturn(Optional.of(true));

		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void webpayReturn_shouldReturnOk_withValidParametersDenied() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000003L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.WAITING, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e3";
		final Webpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, Webpay.PENDING, token);
		when(webpayClient.result(any())).thenReturn(createWebpayResultResponseMock(utilityPaymentTransaction, -1));
		when(webpayClient.ack(any())).thenReturn(Optional.of(true));
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", utilityPaymentWebpay.getToken()))
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
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000004L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.SUCCEEDED, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e4";
		final Webpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, Webpay.ACKNOWLEDGED, token);
		mockMvc.perform(post("/v1/payments/webpay/final").param("token_ws", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTokenWsFailed() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000005L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.FAILED, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e454";
		final Webpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, Webpay.ACKNOWLEDGED, token);
		mockMvc.perform(post("/v1/payments/webpay/final").param("token_ws", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTbkToken() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000006L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.FAILED, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e456";
		final Webpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, Webpay.ACKNOWLEDGED, token);
		mockMvc.perform(post("/v1/payments/webpay/final").param("TBK_TOKEN", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTbkBuyOrder() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000007L;
		final Transaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(Transaction.FAILED, uuid, buyOrder);
		mockMvc.perform(post("/v1/payments/webpay/final").param("TBK_ORDEN_COMPRA", utilityPaymentTransaction.getBuyOrder().toString()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	private Transaction createUtilityPaymentTransactionMock(final String status, final String uuid, final Long buyOrder)
	{
		final Transaction utilityPaymentTransaction = new Transaction();
		utilityPaymentTransaction.setPublicId(uuid);
		utilityPaymentTransaction.setStatus(status);
		utilityPaymentTransaction.setAmount(1000L);
		utilityPaymentTransaction.setEmail("test@multicaja.cl");
		utilityPaymentTransactionService.saveAndRefresh(utilityPaymentTransaction);
		utilityPaymentTransaction.setBuyOrder(buyOrder);
		utilityPaymentTransactionService.save(utilityPaymentTransaction);

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

	private Webpay createUtilityPaymentWebpayMock(final Transaction utilityPaymentTransaction,
		final String status, final String token)
	{
		final Webpay utilityPaymentWebpay = new Webpay();
		utilityPaymentWebpay.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentWebpay.setStatus(status);
		utilityPaymentWebpay.setToken(token);
		utilityPaymentWebpay.setUrl("https://webpay3gint.transbank.cl/webpayserver/initTransaction");
		utilityPaymentWebpayService.save(utilityPaymentWebpay);
		return utilityPaymentWebpay;
	}

	private Optional<WebpayResultResponse> createWebpayResultResponseMock(final Transaction utilityPaymentTransaction,
		final int code)
	{
		final WebpayResultResponse webpayResponse = new WebpayResultResponse();
		webpayResponse.setAccountingDate("0211");
		webpayResponse.setSessionId("fe166a72ee594a40bf232c1deee78c79");
		webpayResponse.setTransactionDate("2019-02-11T14:46:47");
		webpayResponse.setBuyOrder(utilityPaymentTransaction.getBuyOrder().toString());
		webpayResponse.setVci("TSN");
		webpayResponse.setUrlRedirection("http://localhost:7771/v1/payments/webpay/final");
		webpayResponse.setCardNumber("3123");
		webpayResponse.setDetailAmount(new BigDecimal(utilityPaymentTransaction.getAmount()));
		webpayResponse.setDetailAuthorizationCode("1231231");
		webpayResponse.setDetailSharesAmount(new BigDecimal(1000));
		webpayResponse.setDetailSharesNumber(0);
		webpayResponse.setDetailCommerceCode("597020000540");
		webpayResponse.setDetailPaymentTypeCode("VD");
		webpayResponse.setDetailBuyOrder(utilityPaymentTransaction.getBuyOrder().toString());
		webpayResponse.setDetailResponseCode(code);
		return Optional.of(webpayResponse);
	}
}
