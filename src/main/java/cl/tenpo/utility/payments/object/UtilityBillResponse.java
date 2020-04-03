package cl.tenpo.utility.payments.object;

import java.util.List;

public class UtilityBillResponse
{
	private List<UtilityBillItem> bills;
	private boolean unavailable;
	private boolean retry;
	private String retryCollector;

	public boolean isRetry() {
		return retry;
	}

	public void setRetry(final boolean retry) {
		this.retry = retry;
	}

	public String getRetryCollector() {
		return retryCollector;
	}

	public void setRetryCollector(final String retryCollector) {
		this.retryCollector = retryCollector;
	}

	public List<UtilityBillItem> getBills() {
		return bills;
	}

	public void setBills(final List<UtilityBillItem> bills) {
		this.bills = bills;
	}

	public boolean isUnavailable() {
		return unavailable;
	}

	public void setUnavailable(final boolean unavailable) {
		this.unavailable = unavailable;
	}
}
