package cl.tenpo.utility.payments.object;

public class Amount
{
	private Integer currencyCode;
	private Long value;

	public Integer getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(final Integer currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(final Long value) {
		this.value = value;
	}
}
