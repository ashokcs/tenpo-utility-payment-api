package cl.multipay.utility.payments.dto;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class UtilityPaymentRequest
{
	@NotNull
	@NotBlank
	@Pattern(regexp = "^[a-z]{3,10}$")
	private String utility;

	@NotNull
	@NotBlank
	@Pattern(regexp = "^[a-z]{3,10}$")
	private String identifier;

	@NotNull
	@Min(0)
	@Max(999999)
	private Long amount;

	public String getUtility()
	{
		return utility;
	}

	public void setUtility(final String utility)
	{
		this.utility = utility;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}

	public Long getAmount()
	{
		return amount;
	}

	public void setAmount(final Long amount)
	{
		this.amount = amount;
	}
}