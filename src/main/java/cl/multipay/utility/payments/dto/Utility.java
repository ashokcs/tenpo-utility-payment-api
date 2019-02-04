package cl.multipay.utility.payments.dto;

import java.util.List;

public class Utility
{
	private String utility;
	private Collector collector;
	private List<String> identifiers;

	public String getUtility()
	{
		return utility;
	}

	public void setUtility(final String utility)
	{
		this.utility = utility;
	}

	public Collector getCollector()
	{
		return collector;
	}

	public void setCollector(final Collector collector)
	{
		this.collector = collector;
	}

	public List<String> getIdentifiers()
	{
		return identifiers;
	}

	public void setIdentifiers(final List<String> identifiers)
	{
		this.identifiers = identifiers;
	}
}
