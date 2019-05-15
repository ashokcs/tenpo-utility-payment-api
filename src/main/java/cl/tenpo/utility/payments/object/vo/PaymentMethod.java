package cl.tenpo.utility.payments.object.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentMethod
{
	private final Long id;
	@JsonIgnore
	private final String code;
	private final String name;
	@JsonProperty("private")
	private final boolean privateMode;
	@JsonProperty("public")
	private final boolean publicMode;

	public PaymentMethod(
		final Long id,
		final String code,
		final String name,
		final boolean privateMode,
		final boolean publicMode
	){
		this.id = id;
		this.code = code;
		this.name = name;
		this.privateMode = privateMode;
		this.publicMode = publicMode;
	}

	public Long getId() {
		return id;
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
