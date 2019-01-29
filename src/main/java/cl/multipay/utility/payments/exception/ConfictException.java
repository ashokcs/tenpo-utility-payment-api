package cl.multipay.utility.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConfictException extends HttpException
{
	private static final long serialVersionUID = 1L;

	public ConfictException()
	{

	}

	public ConfictException(final String message)
	{
		super(message);
	}
}
