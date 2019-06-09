package cl.tenpo.utility.payments.transaction;

import java.util.List;
import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class TransactionRequest
{
	@NotNull
	@Size(min = 1, max = 15)
	private List<@NotNull UUID> bills;

	public List<UUID> getBills() {
		return bills;
	}

	public void setBills(final List<UUID> bills) {
		this.bills = bills;
	}
}