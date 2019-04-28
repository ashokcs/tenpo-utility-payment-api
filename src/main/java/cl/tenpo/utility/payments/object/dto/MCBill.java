package cl.tenpo.utility.payments.object.dto;

public class MCBill
{
	private String mcCode;
	private Long debtDataId;
	private String dueDate;
	private Long amount;
	private Integer order;
	private String desc;

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

	public Integer getOrder()
	{
		return order;
	}

	public void setOrder(final Integer order)
	{
		this.order = order;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(final String desc)
	{
		this.desc = desc;
	}
}
