package cl.multipay.utility.payments.entity;

import java.time.LocalDateTime;

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
@Table(name = "bills")
@JsonPropertyOrder({"bill_id", "buy_order", "status", "utility", "collector", "identifier", "amount", "due_date", "transaction_id", "payment", "email"})
public class Bill
{
	public static final String PENDING = "pending";
	public static final String WAITING = "waiting";
	public static final String SUCCEED = "succeed";
	public static final String FAILED = "failed";

	public static final String WEBPAY = "webpay";
	public static final String TEF = "tef";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@Column(updatable = false)
	@JsonProperty("bill_id")
	private String publicId;

	@Column(insertable = false)  // TODO updateable false
	private String buyOrder; // TODO show when inserted

	private String status;

	@Column(updatable = false)
	private String utility;

	@Column(updatable = false)
	private String collector;

	@Column(updatable = false)
	private String identifier;

	@Column(updatable = false)
	private Long amount;

	@Column(updatable = false)
	private String dueDate;

	@Column(updatable = false)
	private String transactionId;

	private String payment;

	private String email;

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

	public String getUtility()
	{
		return utility;
	}

	public void setUtility(final String utility)
	{
		this.utility = utility;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(final String identifier)
	{
		this.identifier = identifier;
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

	public String getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(final String dueDate)
	{
		this.dueDate = dueDate;
	}

	public String getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(final String transactionId)
	{
		this.transactionId = transactionId;
	}

	public String getCollector()
	{
		return collector;
	}

	public void setCollector(final String collector)
	{
		this.collector = collector;
	}

	public String getPayment()
	{
		return payment;
	}

	public void setPayment(final String payment)
	{
		this.payment = payment;
	}

	public String getBuyOrder()
	{
		return buyOrder;
	}

	public void setBuyOrder(final String buyOrder)
	{
		this.buyOrder = buyOrder;
	}
}
