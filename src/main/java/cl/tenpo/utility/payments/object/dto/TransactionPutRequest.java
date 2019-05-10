package cl.tenpo.utility.payments.object.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class TransactionPutRequest
{
	@NotBlank
	@Pattern(regexp = "[a-f0-9\\-]{36}")
	private String bill;

	public String getBill()
	{
		return bill;
	}

	public void setBill(final String bill)
	{
		this.bill = bill;
	}
}
