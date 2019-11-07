package cl.tenpo.utility.payments.object;

public class Balance
{
	private Amount balance;
	private boolean updated;

	public Balance(final Integer currencyCode, final Long value, final boolean isUpdated) {
		balance = new Amount();
		balance.setValue(value);
		balance.setCurrencyCode(currencyCode);
		updated = isUpdated;
	}

	public Amount getBalance() {
		return balance;
	}

	public void setBalance(final Amount balance) {
		this.balance = balance;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(final boolean updated) {
		this.updated = updated;
	}
}