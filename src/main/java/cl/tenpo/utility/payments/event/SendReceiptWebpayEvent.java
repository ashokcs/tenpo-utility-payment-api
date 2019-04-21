package cl.tenpo.utility.payments.event;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Webpay;

public class SendReceiptWebpayEvent
{
	private final Transaction utilityPaymentTransaction;
	private final Bill utilityPaymentBill;
	private final Webpay utilityPaymentWebpay;

	public SendReceiptWebpayEvent(final Transaction utilityPaymentTransaction,
		final Bill utilityPaymentBill, final Webpay utilityPaymentWebpay)
	{
		this.utilityPaymentTransaction = utilityPaymentTransaction;
		this.utilityPaymentBill = utilityPaymentBill;
		this.utilityPaymentWebpay = utilityPaymentWebpay;
	}

	public Transaction getUtilityPaymentTransaction()
	{
		return utilityPaymentTransaction;
	}

	public Bill getUtilityPaymentBill()
	{
		return utilityPaymentBill;
	}

	public Webpay getUtilityPaymentWebpay()
	{
		return utilityPaymentWebpay;
	}
}
