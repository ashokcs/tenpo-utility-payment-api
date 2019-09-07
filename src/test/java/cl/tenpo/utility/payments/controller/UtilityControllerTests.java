package cl.tenpo.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.http.impl.client.CloseableHttpClient;
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

import cl.tenpo.utility.payments.entity.Category;
import cl.tenpo.utility.payments.entity.Utility;
import cl.tenpo.utility.payments.object.UtilityBillItem;
import cl.tenpo.utility.payments.repository.CategoryRepository;
import cl.tenpo.utility.payments.repository.UtilityRepository;
import cl.tenpo.utility.payments.util.UtilityClient;
import io.nats.streaming.StreamingConnection;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilityControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CloseableHttpClient client;

	@MockBean
	private StreamingConnection streamingConnection;

	@MockBean
	private UtilityClient utilityClient;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UtilityRepository utilityRepository;

	private final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");

	@Test
	public void categories_shouldReturnOk() throws Exception
	{
		mockMvc.perform(
			get("/v1/utility-payments/categories")
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void utilities_shouldReturnServerError() throws Exception
	{
		when(utilityClient.getUtilities()).thenReturn(Optional.empty());

		mockMvc.perform(
			get("/v1/utility-payments/utilities")
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().is5xxServerError());
	}

	@Test
	public void utilities_shouldReturnOk() throws Exception
	{
		when(utilityClient.getUtilities()).thenReturn(Optional.of("{}"));

		mockMvc.perform(
			get("/v1/utility-payments/utilities")
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void categoryUtilities_shouldReturnNotFound() throws Exception
	{
		mockMvc.perform(
			get("/v1/utility-payments/categories/{id}/utilities", 99l)
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isNotFound());
	}

	@Test
	public void categoryUtilities_shouldReturnOk() throws Exception
	{
		final Category category = createCategory();
		mockMvc.perform(
			get("/v1/utility-payments/categories/{id}/utilities", category.getId())
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void bills_shouldReturnOk_withoutBills() throws Exception
	{
		final Category category = createCategory();
		final Utility utility = createUtility(category);

		final String body = "{\"identifier\":\"ASD123\"}";

		mockMvc.perform(
			post("/v1/utility-payments/utilities/{id}/bills", utility.getId())
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void bills_shouldReturnOk_withBills() throws Exception
	{
		final Category category = createCategory();
		final Utility utility = createUtility(category);
		final String body = "{\"identifier\":\"ASD123\"}";
		when(utilityClient.getBills(any(), any(), any())).thenReturn(bills());
		mockMvc.perform(
			post("/v1/utility-payments/utilities/{id}/bills", utility.getId())
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	private Category createCategory() throws Exception
	{
		final Category category = new Category();
		category.setName("Test");
		category.setQuantity(1l);
		category.setStatus(Category.ENABLED);
		categoryRepository.save(category);
		return category;
	}

	private Utility createUtility(final Category category) throws Exception
	{
		final Utility utility = new Utility();
		utility.setStatus("ENABLED");
		utility.setCategoryId(category.getId());
		utility.setName("Aguas Andinas");
		utility.setCode("AGUAS ANDINAS");
		utility.setCollectorId("3");
		utility.setCollectorName("SENCILLITO");
		utility.setGlossIds("G021, G002");
		utility.setGlossNames("NRO CUENTA, CON DIGITO VERIFICADOR");
		utilityRepository.save(utility);
		return utility;
	}

	private final List<UtilityBillItem> bills()
	{
		final List<UtilityBillItem> bills = new ArrayList<>();
		final UtilityBillItem tmp = new UtilityBillItem();
		tmp.setOrder(1);
		tmp.setMcCode("1231231231");
		tmp.setDebtDataId(1l);
		tmp.setAmount(5000l);
		tmp.setDueDate("No disponible");
		tmp.setOrder(1);
		tmp.setDesc("Deuda total");
		tmp.setDueDate("No disponible");
		bills.add(tmp);
		return bills;
	}
}
