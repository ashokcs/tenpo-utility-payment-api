package cl.multipay.utility.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends HttpException
{
	private static final long serialVersionUID = 1L;

	public InternalServerErrorException()
	{

	}

	public InternalServerErrorException(final String message)
	{
		super(message);
	}
}
