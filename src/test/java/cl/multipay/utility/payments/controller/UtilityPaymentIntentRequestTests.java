package cl.multipay.utility.payments.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.util.Utils;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UtilityPaymentIntentRequestTests
{
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private BillService billService;

	@Test
	public void getPaymentIntent_shouldReturnUnsupportedMediaType() throws Exception
	{
		mockMvc.perform(get("/v1/bills/6c2bba7b4aff418bb576eb180a2b1ea5").contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void getPaymentIntent_shouldReturnNotFound() throws Exception
	{
		mockMvc.perform(get("/v1/bills/6c2bba7b4aff418bb576eb180a2b1ea5").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	public void getPaymentIntent_shouldReturnOk() throws Exception
	{
		final String uuid = createPaymentIntent();
		mockMvc.perform(get("/v1/bills/"+uuid).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.bill_id", Matchers.equalTo(uuid)));
	}

	@Test
	public void createPaymentIntent_shouldReturnOk() throws Exception
	{
		final String json = "{\"utility_id\": 123,\"identifier\": \"123\"}";
		mockMvc.perform(post("/v1/bills").content(json).contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.bill_id").isNotEmpty());
	}

	private String createPaymentIntent()
	{
		final String uuid = Utils.uuid();
		final Bill bill = new Bill();
		bill.setPublicId(uuid);
		bill.setStatus(Bill.STATE_PENDING);
		bill.setUtilityId(123L);
		bill.setIdentifier("123");
		bill.setAmount(1231L);
		bill.setEmail("asd@asd.cl");
		billService.save(bill);
		return uuid;
	}
}
