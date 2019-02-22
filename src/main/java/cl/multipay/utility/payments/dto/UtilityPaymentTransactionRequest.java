package cl.multipay.utility.payments.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class UtilityPaymentTransactionRequest
{
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z\\s\\-\\(\\)\\/_\\.]{2,100}$")
	private String utility;

	@NotBlank
	@Pattern(regexp = "1|2|3|4")
	private String collector;

	@NotBlank
	@Pattern(regexp = "^[0-9]{2,5}$")
	private String category;

	@NotBlank
	@Pattern(regexp = "^[A-Za-z0-9]{2,20}$")
	private String identifier;

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

	public String getCategory()
	{
		return category;
	}

	public void setCategory(final String category)
	{
		this.category = category;
	}
}