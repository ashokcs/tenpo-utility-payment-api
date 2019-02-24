package cl.multipay.utility.payments.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTests
{
	@Autowired
	private EmailService emailService;

	@Test
	public void utilityPaymentReceipt_shouldReturnOk() throws Exception
	{
		emailService.utilityPaymentReceipt();
	}
}
