package cl.multipay.utility.payments.dto;

import java.math.BigDecimal;

public class WebpayResultResponse
{
	private String accountingDate;
	private String sessionId;
	private String transactionDate;
	private String buyOrder;
	private String vci;
	private String urlRedirection;
	private BigDecimal detailAmount;
	private String detailAuthorizationCode;
	private BigDecimal detailSharesAmount;
	private Integer detailSharesNumber;
	private String detailCommerceCode;
	private String detailPaymentTypeCode;
	private String detailBuyOrder;
	private Integer detailResponseCode;
	private String cardNumber;

	public String getAccountingDate()
	{
		return this.accountingDate;
	}

	public void setAccountingDate(final String accountingDate)
	{
		this.accountingDate = accountingDate;
	}

	public String getSessionId()
	{
		return this.sessionId;
	}

	public void setSessionId(final String sessionId)
	{
		this.sessionId = sessionId;
	}

	public String getTransactionDate()
	{
		return this.transactionDate;
	}

	public void setTransactionDate(final String transactionDate)
	{
		this.transactionDate = transactionDate;
	}

	public String getBuyOrder()
	{
		return this.buyOrder;
	}

	public void setBuyOrder(final String buyOrder)
	{
		this.buyOrder = buyOrder;
	}

	public String getVci()
	{
		return this.vci;
	}

	public void setVci(final String vci)
	{
		this.vci = vci;
	}

	public String getUrlRedirection()
	{
		return this.urlRedirection;
	}

	public void setUrlRedirection(final String urlRedirection)
	{
		this.urlRedirection = urlRedirection;
	}

	public BigDecimal getDetailAmount()
	{
		return this.detailAmount;
	}

	public void setDetailAmount(final BigDecimal detailAmount)
	{
		this.detailAmount = detailAmount;
	}

	public String getDetailAuthorizationCode()
	{
		return this.detailAuthorizationCode;
	}

	public void setDetailAuthorizationCode(final String detailAuthorizationCode)
	{
		this.detailAuthorizationCode = detailAuthorizationCode;
	}

	public BigDecimal getDetailSharesAmount()
	{
		return this.detailSharesAmount;
	}

	public void setDetailSharesAmount(final BigDecimal detailSharesAmount)
	{
		this.detailSharesAmount = detailSharesAmount;
	}

	public Integer getDetailSharesNumber()
	{
		return this.detailSharesNumber;
	}

	public void setDetailSharesNumber(final Integer detailSharesNumber)
	{
		this.detailSharesNumber = detailSharesNumber;
	}

	public String getDetailCommerceCode()
	{
		return this.detailCommerceCode;
	}

	public void setDetailCommerceCode(final String detailCommerceCode)
	{
		this.detailCommerceCode = detailCommerceCode;
	}

	public String getDetailPaymentTypeCode()
	{
		return this.detailPaymentTypeCode;
	}

	public void setDetailPaymentTypeCode(final String detailPaymentTypeCode)
	{
		this.detailPaymentTypeCode = detailPaymentTypeCode;
	}

	public String getDetailBuyOrder()
	{
		return this.detailBuyOrder;
	}

	public void setDetailBuyOrder(final String detailBuyOrder)
	{
		this.detailBuyOrder = detailBuyOrder;
	}

	public String getCardNumber()
	{
		return this.cardNumber;
	}

	public void setCardNumber(final String cardNumber)
	{
		this.cardNumber = cardNumber;
	}

	public Integer getDetailResponseCode()
	{
		return this.detailResponseCode;
	}

	public void setDetailResponseCode(final Integer detailResponseCode)
	{
		this.detailResponseCode = detailResponseCode;
	}
}
