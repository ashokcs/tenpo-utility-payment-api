package cl.tenpo.utility.payments.object.dto;

public class TransferenciaStatusResponse
{
	private int orderStatus;
	private String description;
	private String ecoOrderId;

	public int getOrderStatus()
	{
		return orderStatus;
	}

	public void setOrderStatus(final int orderStatus)
	{
		this.orderStatus = orderStatus;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(final String description)
	{
		this.description = description;
	}

	public String getEcoOrderId()
	{
		return ecoOrderId;
	}

	public void setEcoOrderId(final String ecoOrderId)
	{
		this.ecoOrderId = ecoOrderId;
	}
}
