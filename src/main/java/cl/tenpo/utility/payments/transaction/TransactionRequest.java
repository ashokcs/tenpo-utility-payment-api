package cl.tenpo.utility.payments.transaction;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class TransactionRequest
{
	@NotNull
	@Size(min = 1, max = 15)
	private List<@NotBlank @Pattern(regexp = "[a-f0-9\\-]{36}")String> bills;

	public List<String> getBills() {
		return bills;
	}

	public void setBills(final List<String> bills) {
		this.bills = bills;
	}
}