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
@Table(name = "payments")
public class Payment
{
	public static final Long STATUS_PENDING = 0L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@Column(updatable = false)
	@JsonIgnore
	private String publicId;

	@Column(updatable = false)
	@JsonIgnore
	private Long billId;

	@JsonIgnore
	private Long status;

	@Column(updatable = false)
	@JsonIgnore
	private Long orderId;

	@Column(updatable = false)
	private String redirectUrl;

	@Column(insertable = false, updatable = false)
	@JsonIgnore
	private LocalDateTime created;

	@Column(insertable = false)
	@JsonIgnore
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

	public String getPublicId()
	{
		return publicId;
	}

	public void setPublicId(final String publicId)
	{
		this.publicId = publicId;
	}

	public Long getBillId()
	{
		return billId;
	}

	public void setBillId(final Long billId)
	{
		this.billId = billId;
	}

	public Long getStatus()
	{
		return status;
	}

	public void setStatus(final Long status)
	{
		this.status = status;
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
