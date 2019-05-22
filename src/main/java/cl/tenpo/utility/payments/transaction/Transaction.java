package cl.tenpo.utility.payments.transaction;

import java.time.OffsetDateTime;
import java.util.List;

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

import cl.tenpo.utility.payments.bill.Bill;
import cl.tenpo.utility.payments.transferencia.Transferencia;
import cl.tenpo.utility.payments.webpay.Webpay;

@Entity
@Table(name = "transactions")
@JsonPropertyOrder({
	"id",
    "status",
    "order",
    "amount",
    "payment_method",
    "email",
    "bills",
    "webpay",
    "transferencia",
    "created",
    "updated",
})
public class Transaction
{
	public static final String CREATED 	  = "CREATED";
	public static final String PENDING 	  = "PENDING";
	public static final String WAITING 	  = "WAITING";
	public static final String PROCESSING = "PROCESSING";
	public static final String SUCCEEDED  = "SUCCEEDED";
	public static final String FAILED     = "FAILED";
	public static final String EXPIRED 	  = "EXPIRED";

	public static final String WEBPAY = "WEBPAY";
	public static final String TRANSFERENCIA = "TRANSFERENCIA";
	public static final String PREPAID = "PREPAID";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	@JsonIgnore
	private Long id;
	private String status;
	@JsonProperty("id")
	private String publicId;
	@Column(name = "\"order\"", insertable = false)
	private String order;
	private String paymentMethod;
	private Long amount;
	private String email;
	@Column(insertable = false)
	private OffsetDateTime created;
	@Column(insertable = false)
	private OffsetDateTime updated;

	@Transient
    private List<Bill> bills;
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

	public String getOrder() {
		return order;
	}

	public void setOrder(final String order) {
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

	public List<Bill> getBills() {
		return bills;
	}

	public void setBills(final List<Bill> bills) {
		this.bills = bills;
	}
}
