package cl.tenpo.utility.payments.object.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class TransactionCheckoutRequest
{
	@NotNull
	@Min(1)
	@Max(3)
	private Long paymentMethod;

	@NotBlank
	@Email
	private String email;

	public Long getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(final Long paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
}