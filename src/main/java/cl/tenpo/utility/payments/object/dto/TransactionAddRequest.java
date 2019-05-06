package cl.tenpo.utility.payments.object.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class TransactionAddRequest
{
	@NotBlank
	@Pattern(regexp = "[a-f0-9\\-]{36}")
	private String billId;

	public String getBillId()
	{
		return billId;
	}

	public void setBillId(final String billId)
	{
		this.billId = billId;
	}
}
