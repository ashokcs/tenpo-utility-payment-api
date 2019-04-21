package cl.tenpo.utility.payments.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentMethod
{
	@JsonProperty("payment_method_code")
	private String code;
	@JsonProperty("payment_method_name")
	private String name;

	public PaymentMethod(final String code, final String name)
	{
		this.code = code;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}
}
