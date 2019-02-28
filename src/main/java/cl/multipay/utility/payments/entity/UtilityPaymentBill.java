package cl.multipay.utility.payments.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "utility_payment_bills")
public class UtilityPaymentBill
{
	public static final String PENDING = "PENDING";
	public static final String CONFIRMED = "CONFIRMED";
	public static final String FAILED = "FAILED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	private String status;

	@JsonIgnore
	@Column(updatable = false)
	private Long transactionId;

	@Column(updatable = false)
	private String utility;

	@Column(updatable = false)
	private String collector;

	@Column(updatable = false)
	private String category;

	@Column(updatable = false)
	private String identifier;

	@Column(updatable = false)
	private Long dataId;

	@Column(updatable = false)
	private Integer number;

	@Column(name="mc_code_1", updatable = false)
	private String mcCode1;

	@Column(updatable = false)
	private Long amount;

	@Column(updatable = false)
	private String dueDate;

	@JsonIgnore
	@Column(insertable = false, updatable = false)
	private OffsetDateTime created;

	@JsonIgnore
	@Column(insertable = false)
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

	public String getDueDate()
	{
		return dueDate;
	}

	public void setDueDate(final String dueDate)
	{
		this.dueDate = dueDate;
	}

	public String getCollector()
	{
		return collector;
	}

	public void setCollector(final String collector)
	{
		this.collector = collector;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(final String category)
	{
		this.category = category;
	}

	public Long getTransactionId()
	{
		return transactionId;
	}

	public void setTransactionId(final Long transactionId)
	{
		this.transactionId = transactionId;
	}

	public String getMcCode1()
	{
		return mcCode1;
	}

	public void setMcCode1(final String mcCode1)
	{
		this.mcCode1 = mcCode1;
	}

	public Long getDataId()
	{
		return dataId;
	}

	public void setDataId(final Long dataId)
	{
		this.dataId = dataId;
	}

	public Integer getNumber()
	{
		return number;
	}

	public void setNumber(final Integer number)
	{
		this.number = number;
	}
}
