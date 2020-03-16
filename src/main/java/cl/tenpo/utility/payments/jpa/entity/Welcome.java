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
@Table(name = "welcome")
public class Welcome
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	@JsonIgnore
	@Column(name = "\"user\"") private UUID user;
	private Integer visits;
	private Integer tos;
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

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}

	public Integer getVisits() {
		return visits;
	}

	public void setVisits(final Integer visits) {
		this.visits = visits;
	}

	public Integer getTos() {
		return tos;
	}

	public void setTos(final Integer tos) {
		this.tos = tos;
	}
}
