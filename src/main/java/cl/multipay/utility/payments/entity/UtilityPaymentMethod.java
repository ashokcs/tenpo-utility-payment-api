package cl.multipay.utility.payments.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "utility_payment_methods")
public class UtilityPaymentMethod
{
	public static final Long STATE_PENDING = 0l;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@Column(updatable = false)
	@JsonIgnore
	private Long intentId;

	@Column(updatable = false)
	private String uuid;

	private Long state;

	@Column(updatable = false)
	private Long orderId;

	@Column(updatable = false)
	private String redirectUrl;

	@Column(insertable = false, updatable = false)
	private LocalDateTime created;

	@Column(insertable = false)
	private LocalDateTime updated;

	@PreUpdate
	private void preUpdate()
	{
		updated = LocalDateTime.now();
	}

	public Long getId()
	{
		return id;
	}

	public void setId(final Long id)
	{
		this.id = id;
	}

	public Long getIntentId()
	{
		return intentId;
	}

	public void setIntentId(final Long intentId)
	{
		this.intentId = intentId;
	}

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(final String uuid)
	{
		this.uuid = uuid;
	}

	public Long getState()
	{
		return state;
	}

	public void setState(final Long state)
	{
		this.state = state;
	}

	public Long getOrderId()
	{
		return orderId;
	}

	public void setOrderId(final Long orderId)
	{
		this.orderId = orderId;
	}

	public String getRedirectUrl()
	{
		return redirectUrl;
	}

	public void setRedirectUrl(final String redirectUrl)
	{
		this.redirectUrl = redirectUrl;
	}

	public LocalDateTime getCreated()
	{
		return created;
	}

	public void setCreated(final LocalDateTime created)
	{
		this.created = created;
	}

	public LocalDateTime getUpdated()
	{
		return updated;
	}

	public void setUpdated(final LocalDateTime updated)
	{
		this.updated = updated;
	}
}
