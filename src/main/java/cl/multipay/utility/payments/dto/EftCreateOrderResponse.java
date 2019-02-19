package cl.multipay.utility.payments.dto;

public class EftCreateOrderResponse
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
