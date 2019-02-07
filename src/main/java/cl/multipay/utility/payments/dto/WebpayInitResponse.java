package cl.multipay.utility.payments.dto;

public class WebpayInitResponse
{
	private String url;
	private String token;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public String getToken()
	{
		return token;
	}

	public void setToken(final String token)
	{
		this.token = token;
	}
}
