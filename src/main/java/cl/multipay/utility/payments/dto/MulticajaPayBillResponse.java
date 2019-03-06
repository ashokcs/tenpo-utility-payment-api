package cl.multipay.utility.payments.dto;

public class MulticajaPayBillResponse
{
	private String mcCode;
	private String authCode;
	private String date;
	private String hour;

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(final String authCode)
	{
		this.authCode = authCode;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(final String date)
	{
		this.date = date;
	}

	public String getHour()
	{
		return hour;
	}

	public void setHour(final String hour)
	{
		this.hour = hour;
	}

	public String getMcCode()
	{
		return mcCode;
	}

	public void setMcCode(final String mcCode)
	{
		this.mcCode = mcCode;
	}
}
