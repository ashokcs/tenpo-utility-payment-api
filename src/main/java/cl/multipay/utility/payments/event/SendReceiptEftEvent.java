package cl.multipay.utility.payments.event;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentEft;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;

public class SendReceiptEftEvent
{
	private final UtilityPaymentTransaction utilityPaymentTransaction;
	private final UtilityPaymentBill utilityPaymentBill;
	private final UtilityPaymentEft utilityPaymentEft;

	public SendReceiptEftEvent(final UtilityPaymentTransaction utilityPaymentTransaction,
		final UtilityPaymentBill utilityPaymentBill, final UtilityPaymentEft utilityPaymentEft)
	{
		this.utilityPaymentTransaction = utilityPaymentTransaction;
		this.utilityPaymentBill = utilityPaymentBill;
		this.utilityPaymentEft = utilityPaymentEft;
	}

	public UtilityPaymentTransaction getUtilityPaymentTransaction()
	{
		return utilityPaymentTransaction;
	}

	public UtilityPaymentBill getUtilityPaymentBill()
	{
		return utilityPaymentBill;
	}

	public UtilityPaymentEft getUtilityPaymentEft()
	{
		return utilityPaymentEft;
	}
}
