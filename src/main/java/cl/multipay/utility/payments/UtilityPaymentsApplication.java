package cl.multipay.utility.payments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UtilityPaymentsApplication
{

	@Value("${spring.datasource.url}")
	private String dsUrl;

	@Value("${spring.datasource.username}")
	private String dsUser;

	@Value("${spring.datasource.password}")
	private String dsPass;

	@Bean
	public String test()
	{
		System.out.println("--> dsUrl: "+ dsUrl);
		System.out.println("--> dsUser: "+ dsUser);
		System.out.println("--> dsPass: "+ dsPass);
		return "asd";
	}

	public static void main(final String[] args)
	{
		SpringApplication.run(UtilityPaymentsApplication.class, args);
	}
}