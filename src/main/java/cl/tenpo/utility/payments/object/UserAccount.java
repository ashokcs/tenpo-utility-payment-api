package cl.tenpo.utility.payments.object;

import java.math.BigDecimal;
import java.util.UUID;

public class UserAccount
{
	private UUID id;
	private UUID userId;
	private BigDecimal balanceAmount;
	private String balanceCurrency;
	private UUID accountNumber;
	private String accountType;
	private String state;
	private UUID productId;

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(final UUID userId) {
		this.userId = userId;
	}

	public BigDecimal getBalanceAmount() {
		return balanceAmount;
	}

	public void setBalanceAmount(final BigDecimal balanceAmount) {
		this.balanceAmount = balanceAmount;
	}

	public String getBalanceCurrency() {
		return balanceCurrency;
	}

	public void setBalanceCurrency(final String balanceCurrency) {
		this.balanceCurrency = balanceCurrency;
	}

	public UUID getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(final UUID accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(final String accountType) {
		this.accountType = accountType;
	}

	public String getState() {
		return state;
	}

	public void setState(final String state) {
		this.state = state;
	}

	public UUID getProductId() {
		return productId;
	}

	public void setProductId(final UUID productId) {
		this.productId = productId;
	}

	@Override
	public String toString() {
		return "UserAccount [id=" + id + ", userId=" + userId + ", balanceAmount=" + balanceAmount
				+ ", balanceCurrency=" + balanceCurrency + ", accountNumber=" + accountNumber + ", accountType="
				+ accountType + ", state=" + state + ", productId=" + productId + "]";
	}
}
