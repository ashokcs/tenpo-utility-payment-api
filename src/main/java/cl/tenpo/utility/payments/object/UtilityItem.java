package cl.tenpo.utility.payments.object;

import cl.tenpo.utility.payments.entity.Utility;

public class UtilityItem
{
	private Long id;
	private String name;
	private String identifier;
	private String letter;

	public UtilityItem()
	{

	}

	public UtilityItem(final Utility utility)
	{
		this.id = utility.getId();
		this.name = utility.friendlyName();
		this.identifier = utility.getGlossNames();
		this.letter = utility.friendlyName().substring(0, 1).toUpperCase();
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public String getLetter()
	{
		return letter;
	}

	public void setLetter(final String letter)
	{
		this.letter = letter;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}
}
