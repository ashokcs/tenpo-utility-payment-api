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
@Table(name = "webpay_payments")
public class WebpayPayment
{
	public static final Long STATUS_PENDING = 0L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@Column(updatable = false)
	@JsonIgnore
	private Long billId;

	@JsonIgnore
	private Long status;

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

	public Long getStatus()
	{
		return status;
	}

	public void setStatus(final Long status)
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
