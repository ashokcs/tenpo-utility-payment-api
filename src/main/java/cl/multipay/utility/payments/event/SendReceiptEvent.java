package cl.multipay.utility.payments.event;

import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;

public class SendReceiptEvent
{
	private final UtilityPaymentTransaction utilityPaymentTransaction;

	public SendReceiptEvent(final UtilityPaymentTransaction utilityPaymentTransaction)
	{
		this.utilityPaymentTransaction = utilityPaymentTransaction;
	}

	public UtilityPaymentTransaction getUtilityPaymentTransaction()
	{
		return utilityPaymentTransaction;
	}
}
