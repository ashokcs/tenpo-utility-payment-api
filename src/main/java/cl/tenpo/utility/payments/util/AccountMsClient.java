package cl.tenpo.utility.payments.util;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.newrelic.api.agent.NewRelic;

import cl.tenpo.utility.payments.object.UserAccount;

@Service
public class AccountMsClient
{
	private static final Logger logger = LoggerFactory.getLogger(AccountMsClient.class);

	private final Properties properties;

	public AccountMsClient(final Properties properties)
	{
		this.properties = properties;
	}

	public Optional<UserAccount> getAccount(final UUID userId)
	{
		try {
			final RestTemplate restTemplate = new RestTemplate();
			final String url = properties.accountMsUrl.replaceAll("\\{user_id\\}", userId.toString());
			final UserAccount account = restTemplate.getForObject(url, UserAccount.class);
			return Optional.of(account);
		} catch (final HttpClientErrorException e) {
			NewRelic.noticeError(e);
			logger.error("{} - {}", e.getMessage(), e.getStatusCode());
		}
		return Optional.empty();
	}
}
