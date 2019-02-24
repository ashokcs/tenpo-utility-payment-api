package cl.multipay.utility.payments.dto;

import cl.multipay.utility.payments.entity.UtilityPaymentEft;

public class UtilityPaymentEftResponse
{
	private final String url;

	public UtilityPaymentEftResponse(final UtilityPaymentEft utilityPaymentEft)
	{
		this.url = utilityPaymentEft.getUrl();
	}

	public String getUrl()
	{
		return url;
	}
}
