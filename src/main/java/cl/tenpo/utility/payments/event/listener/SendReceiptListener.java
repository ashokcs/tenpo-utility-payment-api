package cl.tenpo.utility.payments.event.listener;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cl.tenpo.utility.payments.event.SendReceiptEftEvent;
import cl.tenpo.utility.payments.event.SendReceiptWebpayEvent;
import cl.tenpo.utility.payments.util.http.SendgridClient;

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
	public void onMessageWebpay(final SendReceiptWebpayEvent event)
	{
		sendgrid.sendReceipt(event);
	}

	@Async
	@EventListener
	public void onMessageEft(final SendReceiptEftEvent event)
	{
		sendgrid.sendReceipt(event);
	}
}
