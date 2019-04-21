package cl.tenpo.utility.payments.jpa.entity;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Entity
@Table(name = "utility_payment_transactions")
@JsonPropertyOrder({
	"id",
    "status",
    "order",
    "payment_method",
    "amount",
    "email",
    "created",
    "updated",
})
public class Transaction
{
	public static final String WAITING = "WAITING";
	public static final String SUCCEEDED = "SUCCEEDED";
	public static final String FAILED = "FAILED";

	public static final String WEBPAY = "WEBPAY";
	public static final String TRANSFERENCIA = "TRANSFERENCIA";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;
	private String status;
	@JsonProperty("id")
	private String publicId;
	@Column(name = "\"order\"", insertable = false)
	@JsonSerialize(using = ToStringSerializer.class)
	private Long order;
	private String paymentMethod;
	private Long amount;
	private String email;
	@Column(insertable = false)
	private OffsetDateTime created;
	@Column(insertable = false)
	private OffsetDateTime updated;

	@Transient
    private Bill bill;
	@Transient
    private Webpay webpay;
	@Transient
    private Transferencia transferencia;

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

	public Long getOrder() {
		return order;
	}

	public void setOrder(final Long order) {
		this.order = order;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(final String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(final Long amount) {
		this.amount = amount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(final String email) {
		this.email = email;
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

	public Bill getBill() {
		return bill;
	}

	public void setBill(final Bill bill) {
		this.bill = bill;
	}

	public Webpay getWebpay() {
		return webpay;
	}

	public void setWebpay(final Webpay webpay) {
		this.webpay = webpay;
	}

	public Transferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(final Transferencia transferencia) {
		this.transferencia = transferencia;
	}
}
