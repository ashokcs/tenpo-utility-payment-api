package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "suggestions")
public class Suggestion
{
	public static final String ENABLED  = "ENABLED";
	public static final String DISABLED = "DISABLED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@JsonIgnore
	private String status;
	@JsonIgnore
	@Column(name = "\"user\"")
	private UUID user;
	@JsonIgnore
	private Long utilityId;
	private String identifier;
	private Long amount;
	@JsonIgnore
	private OffsetDateTime created;
	@JsonIgnore
	@UpdateTimestamp
	private OffsetDateTime updated;
	@JsonIgnore
	private OffsetDateTime expired;

	private transient Utility utility;

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

	public Utility getUtility() {
		return utility;
	}

	public void setUtility(final Utility utility) {
		this.utility = utility;
	}
}
