package cl.tenpo.utility.payments.object.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentMethod
{
	@JsonProperty("code")
	private final String code;
	@JsonProperty("name")
	private final String name;
	@JsonProperty("private")
	private final boolean privateMode;
	@JsonProperty("public")
	private final boolean publicMode;

	public PaymentMethod(
		final String code,
		final String name,
		final boolean privateMode,
		final boolean publicMode
	){
		this.code = code;
		this.name = name;
		this.privateMode = privateMode;
		this.publicMode = publicMode;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public boolean isPrivateMode()
	{
		return privateMode;
	}

	public boolean isPublicMode()
	{
		return publicMode;
	}
}
