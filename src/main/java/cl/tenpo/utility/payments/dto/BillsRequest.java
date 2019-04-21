package cl.tenpo.utility.payments.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class BillsRequest
{
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z\\s\\-\\(\\)\\/_\\.]{2,100}$")
	private String utilityCode;

	@NotBlank
	@Pattern(regexp = "1|2|3|4", message = "Invalid collector")
	private String collectorId;

	@NotBlank
	@Pattern(regexp = "100|200|300|400|500|600|700|800|900|1000|1100|1200|1300", message = "Invalid category")
	private String categoryId;

	@NotBlank
	@Pattern(regexp = "^[A-Z0-9]{2,20}$")
	private String identifier;

	public String getUtilityCode()
	{
		return utilityCode;
	}

	public void setUtilityCode(final String utilityCode)
	{
		this.utilityCode = utilityCode;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
	}

	public String getCollectorId()
	{
		return collectorId;
	}

	public void setCollectorId(final String collectorId)
	{
		this.collectorId = collectorId;
	}

	public String getCategoryId()
	{
		return categoryId;
	}

	public void setCategoryId(final String categoryId)
	{
		this.categoryId = categoryId;
	}
}
