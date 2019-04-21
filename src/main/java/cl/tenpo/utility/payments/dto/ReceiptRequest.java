package cl.tenpo.utility.payments.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ReceiptRequest
{
	@NotBlank
	@Email
	private String email;

	@NotBlank
	private String recaptcha;

	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public String getRecaptcha()
	{
		return recaptcha;
	}

	public void setRecaptcha(final String recaptcha)
	{
		this.recaptcha = recaptcha;
	}
}
