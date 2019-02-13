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
@Table(name = "transferencia_payments")
public class TransferenciaPayment
{
	public static final String PENDING = "PENDING";
	public static final String PAID = "PAID";
	public static final String CANCELED = "CANCELED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@Column(updatable = false)
	@JsonIgnore
	private Long billId;

	@JsonIgnore
	private String status;

	@JsonIgnore
	@Column(updatable = false)
	private String publicId;

	@JsonIgnore
	@Column(updatable = false)
	private String notifyId;

	@Column(updatable = false)
	@JsonIgnore
	private String mcOrderId;

	private String url;

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

	public Long getBillId()
	{
		return billId;
	}

	public void setBillId(final Long billId)
	{
		this.billId = billId;
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

	public String getMcOrderId()
	{
		return mcOrderId;
	}

	public void setMcOrderId(final String mcOrderId)
	{
		this.mcOrderId = mcOrderId;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
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

	public String getNotifyId()
	{
		return notifyId;
	}

	public void setNotifyId(final String notifyId)
	{
		this.notifyId = notifyId;
	}
}
