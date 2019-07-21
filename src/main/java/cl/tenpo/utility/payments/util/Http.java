package cl.tenpo.utility.payments.util;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class Http
{
	/* 400 */
	public static String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";

	/* 404 */
	public static String BILL_ID_NOT_FOUND = "BILL_ID_NOT_FOUND";
	public static String TRANSACTION_ID_NOT_FOUND = "TRANSACTION_ID_NOT_FOUND";
	public static String TRANSFERENCIA_NOT_FOUND = "TRANSFERENCIA_NOT_FOUND";
	public static String WEBPAY_NOT_FOUND = "WEBPAY_NOT_FOUND";

	/* 409 */
	public static String IDENTIFIER_DUPLICATED = "IDENTIFIER_DUPLICATED";
	public static String BILL_ID_DUPLICATED = "BILL_ID_DUPLICATED";
	public static String MAX_SIZE_REACHED = "MAX_SIZE_REACHED";
	public static String FAVORITES_LIMIT_REACHED = "FAVORITES_LIMIT_REACHED";


	public static ResponseStatusException ex(final HttpStatus status)
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
		return ex(HttpStatus.BAD_REQUEST);
	}

	public static ResponseStatusException InvalidPaymentMethod()
	{
		return exception(HttpStatus.BAD_REQUEST, INVALID_PAYMENT_METHOD);
	}

	/* 401 - Unauthorized */

	public static ResponseStatusException Unauthorized()
	{
		return ex(HttpStatus.UNAUTHORIZED);
	}

	/* 404 - NOT FOUND */

	public static ResponseStatusException NotFound()
	{
		return ex(HttpStatus.NOT_FOUND);
	}

	public static ResponseStatusException BillNotFound()
	{
		return exception(HttpStatus.NOT_FOUND, BILL_ID_NOT_FOUND);
	}

	public static ResponseStatusException BillNotFound(final UUID billId)
	{
		return exception(HttpStatus.NOT_FOUND, BILL_ID_NOT_FOUND + ":" + billId.toString());
	}

	public static ResponseStatusException TransactionNotFound()
	{
		return exception(HttpStatus.NOT_FOUND, TRANSACTION_ID_NOT_FOUND);
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
		return exception(HttpStatus.CONFLICT, BILL_ID_DUPLICATED);
	}

	public static ResponseStatusException ConficDuplicatedBillId(final UUID billId)
	{
		return exception(HttpStatus.CONFLICT, BILL_ID_DUPLICATED + ":" + billId.toString());
	}

	public static ResponseStatusException ConficDuplicatedIdentifier()
	{
		return exception(HttpStatus.CONFLICT, IDENTIFIER_DUPLICATED);
	}

	public static ResponseStatusException ConficDuplicatedIdentifier(final UUID billId)
	{
		return exception(HttpStatus.CONFLICT, IDENTIFIER_DUPLICATED + ":" + billId.toString());
	}

	public static ResponseStatusException ConfictMaxSizeReached()
	{
		return exception(HttpStatus.CONFLICT, MAX_SIZE_REACHED);
	}

	public static ResponseStatusException ConfictFavoritesLimitReached()
	{
		return exception(HttpStatus.CONFLICT, FAVORITES_LIMIT_REACHED);
	}

	/* 500 - SERVER ERROR */

	public static ResponseStatusException ServerError()
	{
		return ex(HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
