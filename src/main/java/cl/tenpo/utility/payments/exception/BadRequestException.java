package cl.tenpo.utility.payments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends HttpException
{
	private static final long serialVersionUID = 1L;
}
