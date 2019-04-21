package cl.tenpo.utility.payments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TenpoUtilityPaymentsApplication
{
	public static void main(final String[] args)
	{
		SpringApplication.run(TenpoUtilityPaymentsApplication.class, args);
	}
}