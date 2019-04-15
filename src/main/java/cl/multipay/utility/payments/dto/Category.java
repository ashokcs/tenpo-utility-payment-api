package cl.multipay.utility.payments.dto;

public class Category
{
	private final String id;
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
