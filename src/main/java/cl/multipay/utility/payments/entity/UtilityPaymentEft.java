package cl.multipay.utility.payments.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "utility_payment_eft")
public class UtilityPaymentEft
{
	public static final String PENDING = "PENDING";
	public static final String PAID = "PAID";
	public static final String CANCELED = "CANCELED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@JsonIgnore
	private String status;

	@Column(updatable = false)
	@JsonIgnore
	private Long transactionId;

	@JsonIgnore
	@Column(updatable = false)
	private String publicId;

	@JsonIgnore
	@Column(updatable = false)
	private String notifyId;

	@Column(updatable = false)
	@JsonIgnore
	private String orderId;

	@Column(updatable = false)
	private String url;

	@Column(insertable = false, updatable = false)
	@JsonIgnore
	private ZonedDateTime created;

	@Column(insertable = false)
	@JsonIgnore
	private ZonedDateTime updated;

	@PreUpdate
	private void preUpdate()
	{
		updated = ZonedDateTime.now();
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(final String status)
	{
		this.status = status;
	}

	public String getPublicId()
	{
		return publicId;
	}

	public void setPublicId(final String publicId)
	{
		this.publicId = publicId;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public String getNotifyId()
	{
		return notifyId;
	}

	public void setNotifyId(final String notifyId)
	{
		this.notifyId = notifyId;
	}

	public Long getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(final Long transactionId)
	{
		this.transactionId = transactionId;
	}

	public String getOrderId()
	{
		return orderId;
	}

	public void setOrderId(final String orderId)
	{
		this.orderId = orderId;
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
}
