package cl.multipay.utility.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UtilityPaymentsApplication
{
	public static void main(final String[] args)
	{
		SpringApplication.run(UtilityPaymentsApplication.class, args);
	}
}