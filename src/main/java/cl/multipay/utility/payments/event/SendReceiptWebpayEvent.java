package cl.multipay.utility.payments.event;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;

public class SendReceiptWebpayEvent
{
	private final UtilityPaymentTransaction utilityPaymentTransaction;
	private final UtilityPaymentBill utilityPaymentBill;
	private final UtilityPaymentWebpay utilityPaymentWebpay;

	public SendReceiptWebpayEvent(final UtilityPaymentTransaction utilityPaymentTransaction,
		final UtilityPaymentBill utilityPaymentBill, final UtilityPaymentWebpay utilityPaymentWebpay)
	{
		this.utilityPaymentTransaction = utilityPaymentTransaction;
		this.utilityPaymentBill = utilityPaymentBill;
		this.utilityPaymentWebpay = utilityPaymentWebpay;
	}

	public UtilityPaymentTransaction getUtilityPaymentTransaction()
	{
		return utilityPaymentTransaction;
	}

	public UtilityPaymentBill getUtilityPaymentBill()
	{
		return utilityPaymentBill;
	}

	public UtilityPaymentWebpay getUtilityPaymentWebpay()
	{
		return utilityPaymentWebpay;
	}
}
