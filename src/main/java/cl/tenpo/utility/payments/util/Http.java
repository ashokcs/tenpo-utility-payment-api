package cl.tenpo.utility.payments.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Http
{
	/* 400 */
	public static String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";

	/* 404 */
	public static String BILL_NOT_FOUND = "BILL_NOT_FOUND";
	public static String TRANSACTION_NOT_FOUND = "TRANSACTION_NOT_FOUND";
	public static String TRANSFERENCIA_NOT_FOUND = "TRANSFERENCIA_NOT_FOUND";
	public static String WEBPAY_NOT_FOUND = "WEBPAY_NOT_FOUND";

	/* 409 */
	public static String DUPLICATED_BILL = "DUPLICATED_BILL";
	public static String MAX_SIZE_REACHED = "MAX_SIZE_REACHED";


	public static ResponseStatusException exception(final HttpStatus status)
	{
		return new ResponseStatusException(status);
	}

	public static ResponseStatusException exception(final HttpStatus status, final String message)
	{
		return new ResponseStatusException(status, message);
	}

	/* 400 - BAD REQUEST */

	public static ResponseStatusException BadRequest()
	{
		return exception(HttpStatus.BAD_REQUEST);
	}

	public static ResponseStatusException InvalidPaymentMethod()
	{
		return exception(HttpStatus.BAD_REQUEST, INVALID_PAYMENT_METHOD);
	}

	/* 401 - Unauthorized */

	public static ResponseStatusException Unauthorized()
	{
		return exception(HttpStatus.UNAUTHORIZED);
	}

	/* 404 - NOT FOUND */

	public static ResponseStatusException NotFound()
	{
		return exception(HttpStatus.NOT_FOUND);
	}

	public static ResponseStatusException BillNotFound()
	{
		return exception(HttpStatus.NOT_FOUND, BILL_NOT_FOUND);
	}

	public static ResponseStatusException TransactionNotFound()
	{
		return exception(HttpStatus.NOT_FOUND, TRANSACTION_NOT_FOUND);
	}

	public static ResponseStatusException TransferenciaNotFound()
	{
		return exception(HttpStatus.NOT_FOUND, TRANSFERENCIA_NOT_FOUND);
	}

	public static ResponseStatusException WebpayNotFound()
	{
		return exception(HttpStatus.NOT_FOUND, WEBPAY_NOT_FOUND);
	}

	/* 409 - CONFLICT */

	public static ResponseStatusException ConficDuplicatedBill()
	{
		return exception(HttpStatus.CONFLICT, DUPLICATED_BILL);
	}

	public static ResponseStatusException ConfictMaxSizeReached()
	{
		return exception(HttpStatus.CONFLICT, MAX_SIZE_REACHED);
	}

	/* 500 - SERVER ERROR */

	public static ResponseStatusException ServerError()
	{
		return exception(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
