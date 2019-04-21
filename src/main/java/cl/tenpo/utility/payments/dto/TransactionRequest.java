package cl.tenpo.utility.payments.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class TransactionRequest
{
	@NotBlank
	@Pattern(regexp = "webpay|transferencia")
	private String paymentMethod;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Pattern(regexp = "[a-f0-9\\-]{36}")
	private String bill;

	public String getBill() {
		return bill;
	}

	public void setBill(final String bill) {
		this.bill = bill;
	}

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