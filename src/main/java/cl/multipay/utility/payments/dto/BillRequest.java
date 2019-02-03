package cl.multipay.utility.payments.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class BillRequest
{
	@NotNull
	private Long utilityId;

	@NotBlank
	@Pattern(regexp = "^[a-z0-9]{3,10}$")
	private String identifier;

	public Long getUtilityId()
	{
		return utilityId;
	}

	public void setUtilityId(final Long utilityId)
	{
		this.utilityId = utilityId;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}
}