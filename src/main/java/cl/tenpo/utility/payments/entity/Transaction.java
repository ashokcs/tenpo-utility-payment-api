package cl.tenpo.utility.payments.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "transactions")
@JsonPropertyOrder({
	"id",
    "status",
    "order",
    "user",
    "amount",
    "bills",
    "created",
    "updated",
})
public class Transaction
{
	public static final String PROCESSING = "PROCESSING";
	public static final String SUCCEEDED  = "SUCCEEDED";
	public static final String FAILED     = "FAILED";
	public static final String UNFINISHED = "UNFINISHED";

	@Id
	@Column(updatable = false)
	private UUID id;
	private String status;
	@Column(name = "\"order\"")
	private String order;
	@Column(name = "\"user\"")
	private UUID user;
	private Long amount;
	@JsonIgnore
	private Long amountSucceeded;
	private OffsetDateTime created;
	private OffsetDateTime updated;

	@Transient
    private List<Bill> bills;

	@PrePersist
	private void prePersist()
	{
		id = UUID.randomUUID();
		created = OffsetDateTime.now();
		updated = OffsetDateTime.now();
	}

	@PreUpdate
	private void preUpdate()
	{
		updated = OffsetDateTime.now();
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(final String order) {
		this.order = order;
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

	public List<Bill> getBills() {
		return bills;
	}

	public void setBills(final List<Bill> bills) {
		this.bills = bills;
	}

	public UUID getUser() {
		return user;
	}

	public void setUser(final UUID user) {
		this.user = user;
	}

	public Long getAmountSucceeded() {
		return amountSucceeded;
	}

	public void setAmountSucceeded(final Long amountSucceeded)
	{
		this.amountSucceeded = amountSucceeded;
	}
}
