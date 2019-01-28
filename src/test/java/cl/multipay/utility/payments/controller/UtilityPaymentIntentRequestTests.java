package cl.multipay.utility.payments.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentIntent;
import cl.multipay.utility.payments.service.UtilityPaymentIntentService;

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
	private UtilityPaymentIntentService utilityPaymentIntentService;

	@Test
	public void getPaymentIntent_shouldReturnUnsupportedMediaType() throws Exception
	{
		mockMvc.perform(get("/v1/utilities/payment_intents/6c2bba7b-4aff-418b-b576-eb180a2b1ea5").contentType(MediaType.APPLICATION_XML))
			.andDo(print())
			.andExpect(status().isUnsupportedMediaType());
	}

	@Test
	public void getPaymentIntent_shouldReturnNotFound() throws Exception
	{
		mockMvc.perform(get("/v1/utilities/payment_intents/6c2bba7b-4aff-418b-b576-eb180a2b1ea5").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	public void getPaymentIntent_shouldReturnOk() throws Exception
	{
		final String uuid = createPaymentIntent();
		mockMvc.perform(get("/v1/utilities/payment_intents/"+uuid).contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id", Matchers.equalTo(uuid)));
	}

	@Test
	public void createPaymentIntent_shouldReturnOk() throws Exception
	{
		final String json = "{\"utility\": \"test\",\"identifier\": \"test\"}";
		mockMvc.perform(post("/v1/utilities/payment_intents").content(json).contentType(MediaType.APPLICATION_JSON))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").isNotEmpty());
	}

	private String createPaymentIntent()
	{
		final String uuid = UUID.randomUUID().toString();
		final UtilityPaymentBill utilityPaymentBill = new UtilityPaymentBill();
		utilityPaymentBill.setState(UtilityPaymentBill.STATE_PENDING);
		utilityPaymentBill.setUtility("test");
		utilityPaymentBill.setIdentifier("test");
		final UtilityPaymentIntent utilityPaymentIntent = new UtilityPaymentIntent();
		utilityPaymentIntent.setUuid(uuid);
		utilityPaymentIntent.setState(UtilityPaymentIntent.STATE_PENDING);
		utilityPaymentIntent.setEmail("test@test.test");
		utilityPaymentIntent.getBills().add(utilityPaymentBill);
		utilityPaymentIntentService.save(utilityPaymentIntent);
		return uuid;
	}
}
