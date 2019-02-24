package cl.multipay.utility.payments.dto;

import java.time.ZonedDateTime;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentEft;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;

public class UtilityPaymentTransactionResponse
{
	private final String id;
	private final String status;
	private final Long buyOrder;
	private final Long amount;
	private final String paymentMethod;
	private final String email;
	private final ZonedDateTime created;
	private final ZonedDateTime updated;
	private final UtilityPaymentBill bill;
	private UtilityPaymentWebpay webpay;
	private UtilityPaymentEft eft;

	public UtilityPaymentTransactionResponse(final UtilityPaymentTransaction utilityPaymentTransaction,
		final UtilityPaymentBill utilityPaymentBill)
	{
		this.id = utilityPaymentTransaction.getPublicId();
		this.status = utilityPaymentTransaction.getStatus();
		this.buyOrder = utilityPaymentTransaction.getBuyOrder();
		this.amount = utilityPaymentTransaction.getAmount();
		this.paymentMethod = utilityPaymentTransaction.getPaymentMethod();
		this.email = utilityPaymentTransaction.getEmail();
		this.created = utilityPaymentTransaction.getCreated();
		this.updated = utilityPaymentTransaction.getUpdated();
		this.bill = utilityPaymentBill;
	}

	public String getId()
	{
		return id;
	}

	public String getStatus()
	{
		return status;
	}

	public Long getBuyOrder()
	{
		return buyOrder;
	}

	public Long getAmount()
	{
		return amount;
	}

	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	public String getEmail()
	{
		return email;
	}

	public ZonedDateTime getCreated()
	{
		return created;
	}

	public ZonedDateTime getUpdated()
	{
		return updated;
	}

	public UtilityPaymentBill getBill()
	{
		return bill;
	}

	public UtilityPaymentWebpay getWebpay()
	{
		return webpay;
	}

	public void setWebpay(final UtilityPaymentWebpay webpay)
	{
		this.webpay = webpay;
	}

	public UtilityPaymentEft getEft()
	{
		return eft;
	}

	public void setEft(final UtilityPaymentEft eft)
	{
		this.eft = eft;
	}
}
