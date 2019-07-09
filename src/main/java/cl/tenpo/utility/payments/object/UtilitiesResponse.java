package cl.tenpo.utility.payments.object;

import java.util.List;

public class UtilitiesResponse
{
	private String title;
	private List<UtilityItem> data;

	public String getTitle() {
		return title;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public List<UtilityItem> getData() {
		return data;
	}

	public void setData(final List<UtilityItem> data) {
		this.data = data;
	}
}
