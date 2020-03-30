package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "reminders")
public class Reminder
{
	public static final String ENABLED  = "ENABLED";
	public static final String DISABLED = "DISABLED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String status;
	@Column(name = "\"user\"")
	private UUID user;
	private Long utilityId;
	private String utilityName;
	private String identifier;
	private Long amount;
	private OffsetDateTime created;
	private OffsetDateTime updated;
	private OffsetDateTime expired;

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

	public UUID getUser() {
		return user;
	}

	public void setUser(final UUID user) {
		this.user = user;
	}

	public Long getUtilityId() {
		return utilityId;
	}

	public void setUtilityId(final Long utilityId) {
		this.utilityId = utilityId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(final String identifier) {
		this.identifier = identifier;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(final Long amount) {
		this.amount = amount;
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

	public OffsetDateTime getExpired() {
		return expired;
	}

	public void setExpired(final OffsetDateTime expired) {
		this.expired = expired;
	}

	public String getUtilityName() {
		return utilityName;
	}

	public void setUtilityName(final String utilityName) {
		this.utilityName = utilityName;
	}
}
