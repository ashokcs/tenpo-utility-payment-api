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

	@Column(updatable = false)
	@JsonIgnore
	private Long transactionId;

	@Column(updatable = false)
	private String utility;

	@Column(updatable = false)
	@JsonIgnore
	private String collector;

	@Column(updatable = false)
	@JsonIgnore
	private String category;

	@Column(updatable = false)
	private String identifier;

	@Column(updatable = false)
	@JsonIgnore
	private Long dataId;

	@Column(updatable = false)
	@JsonIgnore
	private Integer number;

	@Column(updatable = false)
	private Long amount;

	@Column(updatable = false)
	private String dueDate;

	@Column(name="mc_code_1")
	@JsonIgnore
	private String mcCode1;

	@Column(name="mc_code_2")
	@JsonIgnore
	private String mcCode2;

	private String authCode;

	@JsonIgnore
	private String date;

	@JsonIgnore
	private String hour;

	@Column(insertable = false, updatable = false)
	@JsonIgnore
	private OffsetDateTime created;

	@Column(insertable = false)
	@JsonIgnore
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

	public String getMcCode2()
	{
		return mcCode2;
	}

	public void setMcCode2(final String mcCode2)
	{
		this.mcCode2 = mcCode2;
	}

	public String getAuthCode()
	{
		return authCode;
	}

	public void setAuthCode(final String authCode)
	{
		this.authCode = authCode;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(final String date)
	{
		this.date = date;
	}

	public String getHour()
	{
		return hour;
	}

	public void setHour(final String hour)
	{
		this.hour = hour;
	}
}
