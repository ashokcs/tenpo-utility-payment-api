package cl.tenpo.utility.payments.object.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class TransactionCheckoutRequest
{
	@NotBlank
	@Pattern(regexp = "[A-Z]{1,20}")
	private String paymentMethod;

	@NotBlank
	@Email
	private String email;

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(final String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
}