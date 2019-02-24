package cl.multipay.utility.payments.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cl.multipay.utility.payments.event.SendReceiptEvent;
import cl.multipay.utility.payments.http.SendgridClient;

@Component
public class SendReceiptListener
{
	private final SendgridClient sendgrid;

	public SendReceiptListener(final SendgridClient sendgrid)
	{
		this.sendgrid = sendgrid;
	}

	@Async
	@EventListener
	public void onMessage(final SendReceiptEvent event)
	{
		sendgrid.sendReceipt(event.getUtilityPaymentTransaction());
	}
}
