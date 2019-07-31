package cl.tenpo.utility.payments.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment
{
	public static final String PROCESSING = "PROCESSING";
	public static final String SUCCEEDED  = "SUCCEEDED";
	public static final String FAILED     = "FAILED";
	public static final String REVERSED   = "REVERSED";

	@Id
	private UUID id;
	private String status;
	private UUID transactionId;
	private UUID billId;
	private UUID externalId;
	private Long paymentMethodId;
	private Long amount;
	private Long paymentId;
	private OffsetDateTime created;
	private OffsetDateTime updated;

	@PrePersist
	private void prePersist()
	{
		id = UUID.randomUUID();
		externalId = UUID.randomUUID();
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

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final UUID transactionId) {
		this.transactionId = transactionId;
	}

	public UUID getBillId() {
		return billId;
	}

	public void setBillId(final UUID billId) {
		this.billId = billId;
	}

	public Long getPaymentMethodId() {
		return paymentMethodId;
	}

	public void setPaymentMethodId(final Long paymentMethodId) {
		this.paymentMethodId = paymentMethodId;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(final Long amount) {
		this.amount = amount;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(final Long paymentId) {
		this.paymentId = paymentId;
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

	public UUID getExternalId() {
		return externalId;
	}

	public void setExternalId(final UUID externalId) {
		this.externalId = externalId;
	}
}
