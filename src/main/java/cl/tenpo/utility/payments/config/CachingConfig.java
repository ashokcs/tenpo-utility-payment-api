package cl.tenpo.utility.payments.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
@Profile({"staging", "sandbox", "production"})
public class CachingConfig
{

}