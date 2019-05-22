package cl.tenpo.utility.payments.job;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;

@Entity
@Table(name = "jobs")
public class Job
{
	public static final String RUNNING  = "RUNNING";
	public static final String FINISHED = "FINISHED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String status;
	private Long transactionId;
	@Version
	private Long version;
	@Column(insertable = false)
	private Integer attempts;
	@Column(insertable = false)
	private OffsetDateTime locked;
	@Column(insertable = false)
	private OffsetDateTime created;
	@Column(insertable = false)
	private OffsetDateTime updated;

	@PreUpdate
	private void preUpdate()
	{
		updated = OffsetDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Long transactionId) {
		this.transactionId = transactionId;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(final Long version) {
		this.version = version;
	}

	public Integer getAttempts() {
		return attempts;
	}

	public void setAttempts(final Integer attempts) {
		this.attempts = attempts;
	}

	public OffsetDateTime getLocked() {
		return locked;
	}

	public void setLocked(final OffsetDateTime locked) {
		this.locked = locked;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public OffsetDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(final OffsetDateTime updated) {
		this.updated = updated;
	}
}
