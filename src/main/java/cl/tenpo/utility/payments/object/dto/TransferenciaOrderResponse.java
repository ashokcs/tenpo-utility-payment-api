package cl.tenpo.utility.payments.object.dto;

public class TransferenciaOrderResponse
{
	private String mcOrderId;
	private String redirectUrl;

	public String getMcOrderId()
	{
		return mcOrderId;
	}

	public void setMcOrderId(final String mcOrderId)
	{
		this.mcOrderId = mcOrderId;
	}

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(final String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}
}
