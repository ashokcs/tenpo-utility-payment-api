package cl.multipay.utility.payments.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "utility_payments")
public class UtilityPayment
{
	public static final Long STATE_PENDING = 0l;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Long id;

	@Column(updatable = false)
	private String uuid;

	@Column(insertable = false, updatable = false)
	private String oc;

	private Long state;

	@Column(insertable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(insertable = false)
	private LocalDateTime updatedAt;

	@PreUpdate
	private void preUpdate()
	{
		updatedAt = LocalDateTime.now();
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

	public LocalDateTime getCreatedAt()
	{
		return createdAt;
	}

	public void setCreatedAt(final LocalDateTime createdAt)
	{
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt()
	{
		return updatedAt;
	}

	public void setUpdatedAt(final LocalDateTime updatedAt)
	{
		this.updatedAt = updatedAt;
	}
}
