package cl.tenpo.utility.payments.event;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;

public class SendReceiptEftEvent
{
	private final Transaction transaction;
	private final Bill bill;
	private final Transferencia transferencia;

	public SendReceiptEftEvent(final Transaction transaction,
		final Bill bill, final Transferencia transferencia)
	{
		this.transaction = transaction;
		this.bill = bill;
		this.transferencia = transferencia;
	}

	public Transaction getTransaction()
	{
		return transaction;
	}

	public Bill getBill()
	{
		return bill;
	}

	public Transferencia getTransferencia()
	{
		return transferencia;
	}
}
