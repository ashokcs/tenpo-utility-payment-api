package cl.tenpo.utility.payments.object.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class TransactionRequest
{
	@NotBlank
	@Pattern(regexp = "webpay|transferencia")
	private String paymentMethodCode;

	@NotBlank
	@Email
	private String email;

	@NotBlank
	@Pattern(regexp = "[a-f0-9\\-]{36}")
	private String billId;

	public String getBillId() {
		return billId;
	}

	public void setBillId(final String billId) {
		this.billId = billId;
	}

	public String getPaymentMethodCode() {
		return paymentMethodCode;
	}

	public void setPaymentMethodCode(final String paymentMethodCode) {
		this.paymentMethodCode = paymentMethodCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}
}