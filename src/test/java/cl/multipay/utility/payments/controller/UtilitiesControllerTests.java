package cl.multipay.utility.payments.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.apache.http.impl.client.CloseableHttpClient;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilitiesControllerTests
{
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CloseableHttpClient client;

	@Test
	public void getUtilities_shouldReturnOk() throws Exception
	{
		final String responseEntity = "{\"response_code\":1,\"response_message\":\"{agreement_data.success}\",\"data\":"
				+ "{\"convenios\":[{\"area\":{\"100\":\"AGUA\"},\"help\":{},\"firm\":\"COSTANERANORTE\",\"gloss\":{},"
				+ "\"collector\":{\"1\":\"OTRO\"}}]}}";
		when(client.execute(any())).thenReturn(new CloseableHttpResponseMock(responseEntity, HttpStatus.OK));

		mockMvc.perform(get("/v1/utilities").contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			.andExpect(status().isOk());
	}
}
