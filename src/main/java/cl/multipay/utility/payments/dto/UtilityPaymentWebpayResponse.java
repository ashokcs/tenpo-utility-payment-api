package cl.multipay.utility.payments.dto;

import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;

public class UtilityPaymentWebpayResponse
{
	private final String token;
	private final String url;

	public UtilityPaymentWebpayResponse(final UtilityPaymentWebpay utilityPaymentWebpay)
	{
		this.token = utilityPaymentWebpay.getToken();
		this.url = utilityPaymentWebpay.getUrl();
	}

	public String getToken()
	{
		return token;
	}

	public String getUrl()
	{
		return url;
	}
}
