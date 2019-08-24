package cl.tenpo.utility.payments.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.nats.streaming.StreamingConnection;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilsTests
{
	@MockBean
	private StreamingConnection streamingConnection;

	@Test
	public void paymentTypeFriendlyName_shouldFormatOk() throws Exception
	{
		assertThat(Utils.paymentTypeFriendlyName(null)).isEqualTo("");
		assertThat(Utils.paymentTypeFriendlyName("")).isEqualTo("Crédito");
		assertThat(Utils.paymentTypeFriendlyName("VP")).isEqualTo("Prepago");
		assertThat(Utils.paymentTypeFriendlyName("VD")).isEqualTo("Débito");
		assertThat(Utils.paymentTypeFriendlyName("VN")).isEqualTo("Crédito");
		assertThat(Utils.paymentTypeFriendlyName("asd")).isEqualTo("Crédito");
	}

	@Test
	public void sharesTypeFriendlyName_shouldFormatOk() throws Exception
	{
		assertThat(Utils.sharesTypeFriendlyName(null)).isEqualTo("");
		assertThat(Utils.sharesTypeFriendlyName("")).isEqualTo("");
		assertThat(Utils.sharesTypeFriendlyName("VN")).isEqualTo("Sin Cuotas");
		assertThat(Utils.sharesTypeFriendlyName("VC")).isEqualTo("Cuotas normales");
		assertThat(Utils.sharesTypeFriendlyName("SI")).isEqualTo("Sin interés");
		assertThat(Utils.sharesTypeFriendlyName("S2")).isEqualTo("Sin interés");
		assertThat(Utils.sharesTypeFriendlyName("NC")).isEqualTo("Sin interés");
		assertThat(Utils.sharesTypeFriendlyName("VD")).isEqualTo("Venta Débito");
		assertThat(Utils.sharesTypeFriendlyName("VP")).isEqualTo("Venta Prepago");
		assertThat(Utils.sharesTypeFriendlyName("ASD")).isEqualTo("");
	}

	@Test
	public void currency_shouldFormatOk() throws Exception
	{
		assertThat(Utils.currency(123)).isEqualTo("$123");
		assertThat(Utils.currency(1234)).isEqualTo("$1.234");
	}

	@Test
	public void utilityFriendlyName_shouldFormatOk() throws Exception
	{
		assertThat(Utils.utilityFriendlyName("ASD_ASD")).isEqualTo("ASD ASD");
		assertThat(Utils.utilityFriendlyName("ASD")).isEqualTo("ASD");
	}
}
