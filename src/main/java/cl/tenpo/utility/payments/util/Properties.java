package cl.tenpo.utility.payments.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Properties
{
	/* multicaja */
	@Value("${multicaja.utilities.apikey}") public String multicajaUtilitiesApiKey;
	@Value("${multicaja.utilities.url}") public String multicajaUtilitiesUrl;
	@Value("${multicaja.utilities.debt.url}") public String multicajaUtlitiesDebtUrl;

	/* job */
	@Value("${job.max.attempts}") public Integer jobMaxAttempts;

	/* nats */
	@Value("${nats.subject.transaction-created}") public String natsTransactionCreated;
	@Value("${nats.subject.suggestions.enabled}") public String natsSuggestionsEnabled;

	/* prepaid */
	@Value("${prepaid.balance.url}") public String prepaidBalanceUrl;

	/* account-ms */
	@Value("${account-ms.account.url}") public String accountMsUrl;

	/* bills */
	@Value("${bills.recently-paid.minus-minutes}") public Integer billsRecentlyPaidMinusMinutes;
	@Value("${bills.max-amount}") public Long billsMaxAmount;
}
