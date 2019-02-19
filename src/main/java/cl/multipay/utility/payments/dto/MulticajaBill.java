package cl.multipay.utility.payments.dto;

public class MulticajaBill
{
	private String mcCode;
	private String dueDate;
	private Long amount;

	public String getMcCode()
	{
		return mcCode;
	}

	public void setMcCode(final String mcCode)
	{
		this.mcCode = mcCode;
	}

	public String getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(final String dueDate)
	{
		this.dueDate = dueDate;
	}

	public Long getAmount()
	{
		return amount;
	}

	public void setAmount(final Long amount)
	{
		this.amount = amount;
	}
}
