package cl.tenpo.utility.payments.webpay;

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

@Entity
@Table(name = "webpay")
@JsonPropertyOrder({
	"id",
	"status",
	"url",
	"token"
})
public class Webpay
{
	public static final String WAITING 		 = "WAITING";
	public static final String RESULTED 	 = "RESULTED";
	public static final String ACKNOWLEDGED  = "ACKNOWLEDGED";
	public static final String FAILED_RESULT = "FAILED_RESULT";
	public static final String FAILED_ACK 	 = "FAILED_ACK";

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	private String status;
	@JsonProperty("id")
	private String publicId;
	@JsonIgnore
	private Long transactionId;
	private String token;
	private String url;
	private Integer code;
	private String authCode;
	private String cardNumber;
	private String paymentType;
	private Integer sharesNumber;
	@JsonIgnore @Column(insertable = false)
	private OffsetDateTime created;
	@JsonIgnore @Column(insertable = false)
	private OffsetDateTime updated;

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

	public Long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(final Long transactionId) {
		this.transactionId = transactionId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(final Integer code) {
		this.code = code;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(final String authCode) {
		this.authCode = authCode;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(final String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(final String paymentType) {
		this.paymentType = paymentType;
	}

	public Integer getSharesNumber() {
		return sharesNumber;
	}

	public void setSharesNumber(final Integer sharesNumber) {
		this.sharesNumber = sharesNumber;
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
}
