package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import cl.tenpo.utility.payments.util.Utils;

@Entity
@Table(name = "utility_payment_bills")
@JsonPropertyOrder({
	"id",
    "status",
    "utility_code",
    "utility_name",
    "collector_id",
    "collector_name",
    "category_id",
    "category_name",
    "identifier",
    "amount",
    "due_date",
    "description"
})
public class Bill
{
	//public static final String PENDING   = "PENDING";
	public static final String WAITING 	 = "WAITING";
	public static final String CONFIRMED = "CONFIRMED";
	public static final String FAILED 	 = "FAILED";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	private String status;
	@JsonProperty("id")
	private String publicId;
	@JsonIgnore
	private Long transactionId;
	@JsonProperty("utility_code")
	private String utility;
	private String collectorId;
	private String collectorName;
	private String categoryId;
	private String categoryName;
	private String identifier;
	private String dueDate;
	private String description;
	private Long amount;
	@JsonIgnore
	private Integer queryOrder;
	@JsonIgnore
	private Long queryId;
	@JsonIgnore
	private String queryTransactionId;
	private Long confirmId;
	private String confirmTransactionId;
	private String confirmState;
	private String confirmAuthCode;
	private String confirmDate;
	private String confirmHour;
	@JsonIgnore @Column(insertable = false)
	private OffsetDateTime created;
	@JsonIgnore @Column(insertable = false)
	private OffsetDateTime updated;

	@JsonProperty("utility_name")
	public String utilityName()
	{
		if (utility != null) {
			return Utils.utilityFriendlyName(utility);
		}
	    return null;
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

	public String getPublicId() {
		return publicId;
	}

	public void setPublicId(final String publicId) {
		this.publicId = publicId;
	}

	public String getUtility() {
		return utility;
	}

	public void setUtility(final String utility) {
		this.utility = utility;
	}

	public String getCollectorId() {
		return collectorId;
	}

	public void setCollectorId(final String collectorId) {
		this.collectorId = collectorId;
	}

	public String getCollectorName() {
		return collectorName;
	}

	public void setCollectorName(final String collectorName) {
		this.collectorName = collectorName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(final String categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
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

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Long transactionId) {
		this.transactionId = transactionId;
	}
}
