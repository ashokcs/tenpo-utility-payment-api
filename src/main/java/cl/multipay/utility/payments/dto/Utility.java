package cl.multipay.utility.payments.dto;

import java.util.List;

public class Utility
{
	private String friendlyName;
	private String utility;
	private String collector;
	private String category;
	private List<String> identifiers;

	public String getUtility()
	{
		return utility;
	}

	public void setUtility(final String utility)
	{
		this.utility = utility;
	}

	public String getCollector()
	{
		return collector;
	}

	public void setCollector(final String collector)
	{
		this.collector = collector;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(final String category)
	{
		this.category = category;
	}

	public List<String> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(final List<String> identifiers)
	{
		this.identifiers = identifiers;
	}

	public String getFriendlyName()
	{
		return friendlyName;
	}

	public void setFriendlyName(final String friendlyName)
	{
		this.friendlyName = friendlyName;
	}
}
