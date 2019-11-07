package cl.tenpo.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	@Value("${time-zone.offset}") public String timezoneOffset;

	/* multicaja */
	@Value("${multicaja.utilities.apikey}") public String multicajaUtilitiesApiKey;
	@Value("${multicaja.utilities.url}") public String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.debt.url}") public String multicajaUtlitiesDebtUrl;

	/* job */
	@Value("${job.max.attempts}") public Integer jobMaxAttempts;

	/* nats */
	@Value("${nats.subject.transaction-created}") public String natsTransactionCreated;

	/* prepaid */
	@Value("${prepaid.balance.url}") public String prepaidBalanceUrl;

	/* account-ms */
	@Value("${account-ms.account.url}") public String accountMsUrl;
}
