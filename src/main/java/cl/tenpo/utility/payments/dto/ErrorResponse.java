package cl.tenpo.utility.payments.dto;

import java.time.LocalDateTime;

public class ErrorResponse
{
	private LocalDateTime timestamp;
	private int status;
	private String error;
	private String message;
	private String path;

	public ErrorResponse(final int status, final String error, final String message, final String path) {
		timestamp = LocalDateTime.now();
		this.status = status;
		this.error = error;
		this.message = message;
		this.path = path;
	}

	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}

	public void setTimestamp(final LocalDateTime timestamp)
	{
		this.timestamp = timestamp;
	}

	public int getStatus()
	{
		return status;
	}

	public void setStatus(final int status)
	{
		this.status = status;
	}

	public String getError()
	{
		return error;
	}

	public void setError(final String error)
	{
		this.error = error;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}

	public String getPath()
	{
		return path;
	}

	public void setPath(final String path)
	{
		this.path = path;
	}
}
