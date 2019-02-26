package cl.multipay.utility.payments.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Profile({"staging", "production"})
public class WebMvcConfig implements WebMvcConfigurer
{
	@Override
	public void addCorsMappings(final CorsRegistry registry)
	{
		registry.addMapping("/**");
	}
}