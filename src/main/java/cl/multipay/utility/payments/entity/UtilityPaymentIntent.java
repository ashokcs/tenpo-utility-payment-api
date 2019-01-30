package cl.multipay.utility.payments.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "utility_payment_intents")
@JsonPropertyOrder({"id","oc","state","email","bills","created"})
public class UtilityPaymentIntent
{
	public static final Long STATE_PENDING = 0L;
	public static final Long STATE_WAITING = 10L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;

	@Column(updatable = false)
	@JsonProperty("id")
	private String uuid;

	@Column(insertable = false, updatable = false)
	private String oc;

	private Long state;

	private String email;

	@Column(insertable = false, updatable = false)
	private LocalDateTime created;

	@Column(insertable = false)
	@JsonIgnore
	private LocalDateTime updated;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "intent_id", referencedColumnName = "id", nullable = false)
	private final List<UtilityPaymentBill> bills = new ArrayList<>();

	@PrePersist
	private void prePersist()
	{
		if ((email != null) && email.equals("")) {
			email = null;
		}
	}

	@PreUpdate
	private void preUpdate()
	{
		updated = LocalDateTime.now();
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

	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(final String uuid)
	{
		this.uuid = uuid;
	}

	public String getOc()
	{
		return oc;
	}

	public void setOc(final String oc)
	{
		this.oc = oc;
	}

	public Long getState()
	{
		return state;
	}

	public void setState(final Long state)
	{
		this.state = state;
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

	public String getEmail()
	{
		return email;
	}

	public void setEmail(final String email)
	{
		this.email = email;
	}

	public List<UtilityPaymentBill> getBills()
	{
		return bills;
	}
}
