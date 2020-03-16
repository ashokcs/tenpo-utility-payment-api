package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "utility_timeouts")
public class UtilityTimeout
{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long utilityId;
	private Integer timeout;
	private OffsetDateTime created;

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Long getUtilityId() {
		return utilityId;
	}

	public void setUtilityId(final Long utilityId) {
		this.utilityId = utilityId;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(final Integer timeout) {
		this.timeout = timeout;
	}

	public OffsetDateTime getCreated() {
		return created;
	}

	public void setCreated(final OffsetDateTime created) {
		this.created = created;
	}
}
