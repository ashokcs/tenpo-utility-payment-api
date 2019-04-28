package cl.tenpo.utility.payments.object.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Category
{
	@JsonProperty("id")
	private final String id;
	@JsonProperty("name")
	private final String name;

	public Category(final String id, final String name)
	{
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
