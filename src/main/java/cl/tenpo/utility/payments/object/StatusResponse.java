package cl.tenpo.utility.payments.object;

public class StatusResponse
{
	private String status;

	public StatusResponse(final String status)
	{
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}
}
