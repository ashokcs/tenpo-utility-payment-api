package cl.multipay.utility.payments.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UtilityPaymentIntentRequest
{
	@NotBlank
	@Pattern(regexp = "^[a-z]{3,10}$")
	private String utility;

	@NotBlank
	@Pattern(regexp = "^[a-z]{3,10}$")
	private String identifier;

	@Email
	private String email;

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

	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}
}