package cl.tenpo.utility.payments.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "favorites")
public class Favorite
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "\"user\"")
	private UUID user;
	private Long utilityId;
	private String identifier;
	private String description;
	private OffsetDateTime created;

	@PrePersist
	private void prePersist()
	{
		created = OffsetDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}
}
