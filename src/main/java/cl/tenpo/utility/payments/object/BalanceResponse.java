package cl.tenpo.utility.payments.object;

import java.util.Optional;

public class BalanceResponse
{
	private Optional<Balance> balance;

	public BalanceResponse()
	{
		balance = Optional.empty();
	}

	public Optional<Balance> getBalance() {
		return balance;
	}

	public void setBalance(final Optional<Balance> balance) {
		this.balance = balance;
	}
}