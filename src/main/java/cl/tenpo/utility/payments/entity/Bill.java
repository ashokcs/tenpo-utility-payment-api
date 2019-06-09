package cl.tenpo.utility.payments.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@Entity
@Table(name = "bills")
@JsonPropertyOrder({
	"id",
    "status",
    "utility",
    "identifier",
    "amount",
    "due_date",
    "description",
    "authorization_code",
    "created"
})
public class Bill
{
	public static final String CREATED 	  = "CREATED";
	public static final String PROCESSING = "PROCESSING";
	public static final String CONFIRMED  = "CONFIRMED";
	public static final String EXPIRED 	  = "EXPIRED";

	@Id
	private UUID id;
	private String status;
	@JsonIgnore
	private UUID transactionId;
	@JsonIgnore
	private Long utilityId;
	private String identifier;
	private String dueDate;
	private String description;
	private Long amount;
	@JsonIgnore
	private Long queryId;
	@JsonIgnore
	private Integer queryOrder;
	@JsonIgnore
	private String queryTransactionId;
	@JsonIgnore
	private Long confirmId;
	@JsonIgnore
	private String confirmTransactionId;
	@JsonIgnore
	private String confirmState;
	@JsonIgnore
	private String confirmAuthCode;
	@JsonIgnore
	private String confirmDate;
	@JsonIgnore
	private String confirmHour;
	private OffsetDateTime created;
	@JsonIgnore
	private OffsetDateTime updated;

	@Transient
	private Utility utility;

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

	@JsonProperty("authorization_code")
	public String authorizationCode()
	{
		if (confirmAuthCode != null && !confirmAuthCode.isEmpty()) {
			return confirmAuthCode;
		} else if (confirmTransactionId != null && !confirmTransactionId.isEmpty()) {
			return confirmTransactionId;
		}
		return null;
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

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(final String dueDate) {
		this.dueDate = dueDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(final Long amount) {
		this.amount = amount;
	}

	public Integer getQueryOrder() {
		return queryOrder;
	}

	public void setQueryOrder(final Integer queryOrder) {
		this.queryOrder = queryOrder;
	}

	public Long getQueryId() {
		return queryId;
	}

	public void setQueryId(final Long queryId) {
		this.queryId = queryId;
	}

	public String getQueryTransactionId() {
		return queryTransactionId;
	}

	public void setQueryTransactionId(final String queryTransactionId) {
		this.queryTransactionId = queryTransactionId;
	}

	public Long getConfirmId() {
		return confirmId;
	}

	public void setConfirmId(final Long confirmId) {
		this.confirmId = confirmId;
	}

	public String getConfirmTransactionId() {
		return confirmTransactionId;
	}

	public void setConfirmTransactionId(final String confirmTransactionId) {
		this.confirmTransactionId = confirmTransactionId;
	}

	public String getConfirmState() {
		return confirmState;
	}

	public void setConfirmState(final String confirmState) {
		this.confirmState = confirmState;
	}

	public String getConfirmAuthCode() {
		return confirmAuthCode;
	}

	public void setConfirmAuthCode(final String confirmAuthCode) {
		this.confirmAuthCode = confirmAuthCode;
	}

	public String getConfirmDate() {
		return confirmDate;
	}

	public void setConfirmDate(final String confirmDate) {
		this.confirmDate = confirmDate;
	}

	public String getConfirmHour() {
		return confirmHour;
	}

	public void setConfirmHour(final String confirmHour) {
		this.confirmHour = confirmHour;
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

	public UUID getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final UUID transactionId) {
		this.transactionId = transactionId;
	}

	public Utility getUtility() {
		return utility;
	}

	public void setUtility(final Utility utility) {
		this.utility = utility;
	}
}
