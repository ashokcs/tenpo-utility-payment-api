package cl.multipay.utility.payments.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class BillRequest
{
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z\\s\\-\\(\\)\\/_\\.]{3,100}$")
	private String utility;

	@NotBlank
	@Pattern(regexp = "^[a-z0-9]{3,20}$")
	private String identifier;

	@NotBlank
	@Pattern(regexp = "^[0-9]{1}$")
	private String collector;

	public String getUtility()
	{
		return utility;
	}

	public void setUtility(final String utility)
	{
		this.utility = utility;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}

	public String getCollector()
	{
		return collector;
	}

	public void setCollector(final String collector)
	{
		this.collector = collector;
	}
}