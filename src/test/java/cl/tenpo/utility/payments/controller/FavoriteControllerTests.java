package cl.tenpo.utility.payments.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

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

import cl.tenpo.utility.payments.entity.Favorite;
import cl.tenpo.utility.payments.entity.Utility;
import cl.tenpo.utility.payments.repository.FavoriteRepository;
import cl.tenpo.utility.payments.repository.UtilityRepository;
import io.nats.streaming.StreamingConnection;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class FavoriteControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StreamingConnection streamingConnection;

	@Autowired
	private UtilityRepository utilityRepository;

	@Autowired
	private FavoriteRepository favoriteRepository;

	private final UUID user = UUID.fromString("470e40c5-89f3-4500-ac62-7991c04d24d5");

	@Test
	public void index_shouldReturnOk() throws Exception
	{
		mockMvc.perform(
			get("/v1/utility-payments/favorites")
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	@Test
	public void create_shouldReturnOk() throws Exception
	{
		final String body = "{\"utility_id\": 1, \"identifier\":\"ASD123\"}";
		createUtility();

		mockMvc.perform(
			post("/v1/utility-payments/favorites")
			.header("x-mine-user-id", user.toString())
			.content(body)
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isCreated());
	}

	@Test
	public void delete_shouldReturnOk() throws Exception
	{
		final Utility utility = createUtility();
		createFavorite(utility);

		mockMvc.perform(
			delete("/v1/utility-payments/favorites/{id}", 1l)
			.header("x-mine-user-id", user.toString())
			.contentType(MediaType.APPLICATION_JSON)
		)
		.andDo(print())
		.andExpect(status().isOk());
	}

	private Utility createUtility() throws Exception
	{
		final Utility utility = new Utility();
		utility.setId(1l);
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

	private void createFavorite(final Utility utility) throws Exception
	{
		final Favorite favorite = new Favorite();
		favorite.setUser(user);
		favorite.setUtility(utility);
		favorite.setIdentifier("123ASD");
		favoriteRepository.save(favorite);
	}
}
