package cl.multipay.utility.payments.event;

public class TotaliserEvent
{
	private final Long amount;

	public TotaliserEvent(final Long amount)
	{
		this.amount = amount;
	}

	public Long getAmount()
	{
		return amount;
	}
}
