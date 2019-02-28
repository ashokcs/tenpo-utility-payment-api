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
import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;
import cl.multipay.utility.payments.http.WebpayClient;
import cl.multipay.utility.payments.service.UtilityPaymentBillService;
import cl.multipay.utility.payments.service.UtilityPaymentTransactionService;
import cl.multipay.utility.payments.service.UtilityPaymentWebpayService;
import cl.multipay.utility.payments.util.Properties;
import cl.multipay.utility.payments.util.Utils;

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
	private UtilityPaymentTransactionService utilityPaymentTransactionService;

	@Autowired
	private UtilityPaymentBillService utilityPaymentBillService;

	@Autowired
	private UtilityPaymentWebpayService utilityPaymentWebpayService;

	@Autowired
	private Utils utils;

	@MockBean
	private WebpayClient webpayClient;

	@Test
	public void webpayReturn_shouldReturnFound_withNoParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/return"))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(header().string("Location", properties.webpayRedirectError));
	}

	@Test
	public void webpayReturn_shouldReturnFound_withInvalidParameters() throws Exception
	{
		mockMvc.perform(post("/v1/payments/webpay/return").param("token_ws", "65as4d5sad65asf46fddff"))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(header().string("Location", properties.webpayRedirectError));
	}

	@Test
	public void webpayReturn_shouldReturnFound_withValidParameters() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000001L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e1";
		final UtilityPaymentWebpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, UtilityPaymentWebpay.PENDING, token);
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
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e2";
		final UtilityPaymentWebpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, UtilityPaymentWebpay.PENDING, token);
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
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.WAITING, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e3";
		final UtilityPaymentWebpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, UtilityPaymentWebpay.PENDING, token);
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
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.SUCCEEDED, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e4e4";
		final UtilityPaymentWebpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, UtilityPaymentWebpay.ACKNOWLEDGED, token);
		mockMvc.perform(post("/v1/payments/webpay/final").param("token_ws", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTokenWsFailed() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000005L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.FAILED, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e454";
		final UtilityPaymentWebpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, UtilityPaymentWebpay.ACKNOWLEDGED, token);
		mockMvc.perform(post("/v1/payments/webpay/final").param("token_ws", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTbkToken() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000006L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.FAILED, uuid, buyOrder);
		final String token = "ecf517e45c7e103b51e532a73183a8b3b003a75075a9347e0895613598d8e456";
		final UtilityPaymentWebpay utilityPaymentWebpay = createUtilityPaymentWebpayMock(utilityPaymentTransaction, UtilityPaymentWebpay.ACKNOWLEDGED, token);
		mockMvc.perform(post("/v1/payments/webpay/final").param("TBK_TOKEN", utilityPaymentWebpay.getToken()))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	public void webpayFinal_shouldReturnFound_withTbkBuyOrder() throws Exception
	{
		final String uuid = utils.uuid();
		final Long buyOrder = 1201902112113000007L;
		final UtilityPaymentTransaction utilityPaymentTransaction = createUtilityPaymentTransactionMock(UtilityPaymentTransaction.FAILED, uuid, buyOrder);
		mockMvc.perform(post("/v1/payments/webpay/final").param("TBK_ORDEN_COMPRA", utilityPaymentTransaction.getBuyOrder().toString()))
			.andDo(print())
			.andExpect(status().isFound());
	}

	private UtilityPaymentTransaction createUtilityPaymentTransactionMock(final String status, final String uuid, final Long buyOrder)
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
		utilityPaymentBill.setMcCode1("12312312321");
		utilityPaymentBill.setAmount(123123L);
		utilityPaymentBill.setDueDate("2019-06-06");
		utilityPaymentBill.setMcCode1("123123123");
		utilityPaymentBillService.save(utilityPaymentBill);

		return utilityPaymentTransaction;
	}

	private UtilityPaymentWebpay createUtilityPaymentWebpayMock(final UtilityPaymentTransaction utilityPaymentTransaction,
		final String status, final String token)
	{
		final UtilityPaymentWebpay utilityPaymentWebpay = new UtilityPaymentWebpay();
		utilityPaymentWebpay.setTransactionId(utilityPaymentTransaction.getId());
		utilityPaymentWebpay.setStatus(status);
		utilityPaymentWebpay.setToken(token);
		utilityPaymentWebpay.setUrl("https://webpay3gint.transbank.cl/webpayserver/initTransaction");
		utilityPaymentWebpayService.save(utilityPaymentWebpay);
		return utilityPaymentWebpay;
	}

	private Optional<WebpayResultResponse> createWebpayResultResponseMock(final UtilityPaymentTransaction utilityPaymentTransaction,
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
