package cl.tenpo.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import cl.tenpo.utility.payments.entity.Bill;
import cl.tenpo.utility.payments.entity.Utility;
import cl.tenpo.utility.payments.object.Balance;
import cl.tenpo.utility.payments.object.BalanceResponse;
import cl.tenpo.utility.payments.repository.BillRepository;
import cl.tenpo.utility.payments.repository.JobRepository;
import cl.tenpo.utility.payments.repository.PaymentRepository;
import cl.tenpo.utility.payments.repository.TransactionRepository;
import cl.tenpo.utility.payments.repository.UtilityRepository;
import cl.tenpo.utility.payments.util.PrepaidClient;
import io.nats.streaming.StreamingConnection;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class TransactionControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CloseableHttpClient client;

	@MockBean
	private StreamingConnection streamingConnection;

	@MockBean
	private PrepaidClient prepaidClient;

	@Autowired
	private BillRepository billRepository;

	@Autowired
	private UtilityRepository utilityRepository;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private JobRepository jobRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Test
	public void create_shouldReturnOk() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final Utility u = createUtility();
		final Bill b = createBill(user, "123123ASD", u);

		when(prepaidClient.balance(any())).thenReturn(mockBalance(99999l));

		final String body = "{\"bills\": [\""+b.getId().toString()+"\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void create_shouldReturnConflict_withDuplicatedBills() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final String body = "{\"bills\": [\"470e40c5-89f3-4500-ac62-7991c04d24d5\", \"470e40c5-89f3-4500-ac62-7991c04d24d5\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isConflict());
	}

	@Test
	public void create_shouldReturnNotFound() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final String body = "{\"bills\": [\"470e40c5-89f3-4500-ac62-7991c04d24d5\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isNotFound());
	}

	@Test
	public void create_shouldReturnConflict_withDuplicatedIdentifiers() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final Utility u = createUtility();
		final Bill b1 = createBill(user, "123123ASD", u);
		final Bill b2 = createBill(user, "123123ASD", u);

		final String body = "{\"bills\": [\""+b1.getId()+"\", \""+b2.getId()+"\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isConflict());
	}

	@Test
	public void create_shouldReturnOk_withTwoBillsDifferentUtility() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final Utility u1 = createUtility();
		final Utility u2 = createUtility();
		final Bill b1 = createBill(user, "123123ASD1", u1);
		final Bill b2 = createBill(user, "123123ASD2", u2);

		when(prepaidClient.balance(any())).thenReturn(mockBalance(99999l));

		final String body = "{\"bills\": [\""+b1.getId()+"\", \""+b2.getId()+"\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void create_shouldReturnOk_withTwoBillsSameUtility() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final Utility u = createUtility();
		final Bill b1 = createBill(user, "123123ASD1", u);
		final Bill b2 = createBill(user, "123123ASD2", u);

		when(prepaidClient.balance(any())).thenReturn(mockBalance(99999l));

		final String body = "{\"bills\": [\""+b1.getId()+"\", \""+b2.getId()+"\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void create_shouldReturnServerError_withErrorBalance() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final Utility u = createUtility();
		final Bill b1 = createBill(user, "123123ASD", u);

		when(prepaidClient.balance(any())).thenReturn(new BalanceResponse());

		final String body = "{\"bills\": [\""+b1.getId()+"\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().is5xxServerError());
	}

	@Test
	public void create_shouldReturnConflict_withNotEnoughBalance() throws Exception
	{
		final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");
		final Utility u = createUtility();
		final Bill b1 = createBill(user, "123123ASD", u);

		when(prepaidClient.balance(any())).thenReturn(mockBalance(0l));

		final String body = "{\"bills\": [\""+b1.getId()+"\"]}";
		mockMvc.perform(
			post("/v1/utility-payments/transactions")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isConflict());
	}

	private Utility createUtility() throws Exception
	{
		final Utility utility = new Utility();
		utility.setStatus("ENABLED");
		utility.setCategoryId(1l);
		utility.setName("Aguas Andinas");
		utility.setCode("AGUAS ANDINAS");
		utility.setCollectorId("3");
		utility.setCollectorName("SENCILLITO");
		utility.setGlossIds("G021, G002");
		utility.setGlossNames("NRO CUENTA, CON DIGITO VERIFICADOR");
		utilityRepository.save(utility);
		return utility;
	}

	private Bill createBill(final UUID user, final String identifier, final Utility utility) throws Exception
	{
		transactionTemplate.execute(status -> {
			entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS public.transactions_seq MAXVALUE 999 CYCLE;").executeUpdate();
			status.flush();
			return null;
		});

		final Bill bill = new Bill();
		bill.setStatus(Bill.CREATED);
		bill.setUser(user);
		bill.setUtilityId(utility.getId());
		bill.setIdentifier(identifier);
		bill.setDueDate("No disponible");
		bill.setDescription("Deuda Total");
		bill.setAmount(1000l);
		bill.setQueryId(1l);
		bill.setQueryOrder(1);
		bill.setQueryTransactionId("19287391273");
		billRepository.save(bill);
		return bill;
	}

	private BalanceResponse mockBalance(final Long amount)
	{
		final BalanceResponse br = new BalanceResponse();
		br.setBalance(Optional.of(new Balance(156, amount, true)));
		return br;
	}

	@After
	public void clean()
	{
		paymentRepository.deleteAll();
		billRepository.deleteAll();
		jobRepository.deleteAll();
		transactionRepository.deleteAll();
		utilityRepository.deleteAll();
		entityManager.close();
	}
}
