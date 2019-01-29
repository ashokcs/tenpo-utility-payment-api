package cl.multipay.utility.payments.exception;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import cl.multipay.utility.payments.dto.ErrorResponse;

@RestController
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler
{
	private @Autowired HttpServletRequest request;

	@ExceptionHandler(HttpException.class)
	public final ResponseEntity<ErrorResponse> handleHttpException(final HttpException ex)
	{
		HttpStatus status;
		if (ex instanceof NotFoundException) {
			status = HttpStatus.NOT_FOUND;
		} else if (ex instanceof ConfictException) {
			status = HttpStatus.CONFLICT;
		} else {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}

		final ErrorResponse errorResponse = new ErrorResponse(status.value(), status.getReasonPhrase(), ex.getMessage(), request.getRequestURI().toString());
		return new ResponseEntity<>(errorResponse, status);
	}
}