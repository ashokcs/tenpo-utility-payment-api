package cl.tenpo.utility.payments.event;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Webpay;

public class SendReceiptWebpayEvent
{
	private final Bill bill;
	private final Transaction transaction;
	private final Webpay webpay;

	public SendReceiptWebpayEvent(
		final Bill bill,
		final Transaction transaction,
		final Webpay webpay)
	{
		this.transaction = transaction;
		this.bill = bill;
		this.webpay = webpay;
	}

	public Transaction getTransaction()
	{
		return transaction;
	}

	public Bill getBill()
	{
		return bill;
	}

	public Webpay getWebpay()
	{
		return webpay;
	}
}
