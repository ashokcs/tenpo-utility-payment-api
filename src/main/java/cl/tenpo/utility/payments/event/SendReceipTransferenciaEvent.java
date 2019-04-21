package cl.tenpo.utility.payments.event;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;

public class SendReceipTransferenciaEvent
{
	private final Bill bill;
	private final Transaction transaction;
	private final Transferencia transferencia;

	public SendReceipTransferenciaEvent(
		final Bill bill,
		final Transaction transaction,
		final Transferencia transferencia
	){
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
