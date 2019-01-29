package cl.multipay.utility.payments.dto;

public class MulticajaCreateOrderResponse
{
	private Long orderId;
	private String status;
	private String redirectUrl;
	private String referenceId;

	public Long getOrderId()
	{
		return orderId;
	}

	public void setOrderId(final Long orderId)
	{
		this.orderId = orderId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(final String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}

	public String getReferenceId()
	{
		return referenceId;
	}

	public void setReferenceId(final String referenceId)
	{
		this.referenceId = referenceId;
	}
}
