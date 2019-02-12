package cl.multipay.utility.payments.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class BillPayRequest
{
	@NotBlank
	@Email
	private String email;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}
}
