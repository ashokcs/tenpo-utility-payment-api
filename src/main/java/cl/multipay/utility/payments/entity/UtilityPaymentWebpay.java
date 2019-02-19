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
@Table(name = "utility_payment_webpay")
public class UtilityPaymentWebpay
{
	public static final String PENDING = "PENDING";
	public static final String RESULTED = "RESULTED";
	public static final String ACKNOWLEDGED = "ACKNOWLEDGED";

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

	@Column(updatable = false)
	private String token;

	@Column(updatable = false)
	private String url;

	private Integer responseCode;

	private String authCode;

	private String card;

	private String paymentType;

	private Integer shares;

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

	public String getToken()
	{
		return token;
	}

	public void setToken(final String token)
	{
		this.token = token;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(final String url)
	{
		this.url = url;
	}

	public Integer getResponseCode()
	{
		return responseCode;
	}

	public void setResponseCode(final Integer responseCode)
	{
		this.responseCode = responseCode;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(final String authCode)
	{
		this.authCode = authCode;
	}

	public String getCard()
	{
		return card;
	}

	public void setCard(final String card)
	{
		this.card = card;
	}

	public String getPaymentType()
	{
		return paymentType;
	}

	public void setPaymentType(final String paymentType)
	{
		this.paymentType = paymentType;
	}

	public Integer getShares()
	{
		return shares;
	}

	public void setShares(final Integer shares)
	{
		this.shares = shares;
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

	public Long getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(final Long transactionId)
	{
		this.transactionId = transactionId;
	}
}
