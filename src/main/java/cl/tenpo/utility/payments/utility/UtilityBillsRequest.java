package cl.tenpo.utility.payments.utility;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UtilityBillsRequest
{
	@NotBlank
	@Pattern(regexp = "^[A-Z0-9]{2,20}$")
	private String identifier;

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}
}
