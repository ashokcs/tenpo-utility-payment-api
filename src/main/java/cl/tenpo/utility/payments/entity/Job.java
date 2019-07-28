package cl.tenpo.utility.payments.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
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
	private UUID transactionId;
	@Version
	private Long version;
	@Column(insertable = false)
	private Integer attempt;
	private Integer attempts;
	@Column(insertable = false)
	private OffsetDateTime locked;
	private OffsetDateTime created;
	private OffsetDateTime updated;

	public Job()
	{

	}

	public Job(final UUID id, final Integer attempts)
	{
		setStatus(RUNNING);
		setTransactionId(id);
		setAttempts(attempts);
	}

	@PrePersist
	private void prePersist()
	{
		created = OffsetDateTime.now();
		updated = OffsetDateTime.now();
	}

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

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final UUID transactionId) {
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

	public Integer getAttempt() {
		return attempt;
	}

	public void setAttempt(final Integer attempt) {
		this.attempt = attempt;
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
