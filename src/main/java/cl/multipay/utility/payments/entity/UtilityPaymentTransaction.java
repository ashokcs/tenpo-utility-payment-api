package cl.multipay.utility.payments.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "utility_payment_transactions")
@JsonPropertyOrder({"id", "status", "buy_order", "amount", "payment_method", "email"})
public class UtilityPaymentTransaction
{
	public static final String PENDING = "PENDING";
	public static final String WAITING = "WAITING";
	public static final String SUCCEEDED = "SUCCEEDED";
	public static final String FAILED = "FAILED";

	public static final String WEBPAY = "WEBPAY";
	public static final String EFT = "EFT";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	private String status;

	@Column(updatable = false)
	@JsonProperty("id")
	private String publicId;

	@Column(insertable = false)
	private Long buyOrder;

	@Column(updatable = false)
	private Long amount;

	private String paymentMethod;

	private String email;

	@Column(insertable = false, updatable = false)
	private ZonedDateTime created;

	@Column(insertable = false)
	private ZonedDateTime updated;

	@PreUpdate
	private void preUpdate()
	{
		updated = ZonedDateTime.now();
	}

	@PrePersist
	private void prePersist()
	{
		if ((email != null) && email.equals("")) {
			email = null;
		}
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public String getPublicId()
	{
		return publicId;
	}

	public void setPublicId(final String publicId)
	{
		this.publicId = publicId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public Long getAmount()
	{
		return amount;
	}

	public void setAmount(final Long amount)
	{
		this.amount = amount;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public ZonedDateTime getCreated()
	{
		return created;
	}

	public void setCreated(final ZonedDateTime created)
	{
		this.created = created;
	}

	public ZonedDateTime getUpdated()
	{
		return updated;
	}

	public void setUpdated(final ZonedDateTime updated)
	{
		this.updated = updated;
	}

	public Long getBuyOrder()
	{
		return buyOrder;
	}

	public void setBuyOrder(final Long buyOrder)
	{
		this.buyOrder = buyOrder;
	}

	public String getPaymentMethod()
	{
		return paymentMethod;
	}

	public void setPaymentMethod(final String paymentMethod)
	{
		this.paymentMethod = paymentMethod;
	}
}
