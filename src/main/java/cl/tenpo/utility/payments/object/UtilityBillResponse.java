package cl.tenpo.utility.payments.object;

import java.util.List;

public class UtilityBillResponse
{
	private List<UtilityBillItem> bills;
	private boolean unavailable;

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
