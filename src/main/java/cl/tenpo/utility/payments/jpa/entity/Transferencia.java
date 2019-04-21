package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "utility_payment_transferencia")
@JsonPropertyOrder({
	"id",
	"status",
	"url",
	"order"
})
public class Transferencia
{
	public static final String WAITING  = "WAITING";
	public static final String PAID 	= "PAID";
	public static final String CANCELED = "CANCELED";
	public static final String FAILED   = "FAILED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	private String status;
	@JsonProperty("id")
	private String publicId;
	@JsonIgnore
	private Long transactionId;
	@JsonIgnore
	private String notifyId;
	private String url;
	@Column(name = "\"order\"")
	private String order;
	@JsonIgnore @Column(insertable = false)
	private OffsetDateTime created;
	@JsonIgnore @Column(insertable = false)
	private OffsetDateTime updated;

	@PreUpdate
	private void preUpdate()
	{
		updated = OffsetDateTime.now();
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

	public String getOrder()
	{
		return order;
	}

	public void setOrder(final String order)
	{
		this.order = order;
	}

	public OffsetDateTime getCreated()
	{
		return created;
	}

	public void setCreated(final OffsetDateTime created)
	{
		this.created = created;
	}

	public OffsetDateTime getUpdated()
	{
		return updated;
	}

	public void setUpdated(final OffsetDateTime updated)
	{
		this.updated = updated;
	}
}
