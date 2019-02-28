package cl.multipay.utility.payments.dto;

public class MulticajaBillResponse
{
	private String mcCode;
	private Long debtDataId;
	private String dueDate;
	private Long amount;
	private Integer debtNumber;

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

	public Long getDebtDataId()
	{
		return debtDataId;
	}

	public void setDebtDataId(final Long debtDataId)
	{
		this.debtDataId = debtDataId;
	}

	public Integer getDebtNumber()
	{
		return debtNumber;
	}

	public void setDebtNumber(final Integer debtNumber)
	{
		this.debtNumber = debtNumber;
	}
}
