package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "options")
public class Option
{
	@JsonIgnore
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "\"user\"") @JsonIgnore
	private UUID user;
	private boolean suggest;
	private boolean remind;
	private Integer remindFrequency;
	@JsonIgnore
	private OffsetDateTime created;

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

	public boolean isSuggest() {
		return suggest;
	}

	public void setSuggest(final boolean suggest) {
		this.suggest = suggest;
	}

	public boolean isRemind() {
		return remind;
	}

	public void setRemind(final boolean remind) {
		this.remind = remind;
	}

	public Integer getRemindFrequency() {
		return remindFrequency;
	}

	public void setRemindFrequency(final Integer remindFrequency) {
		this.remindFrequency = remindFrequency;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}
}
