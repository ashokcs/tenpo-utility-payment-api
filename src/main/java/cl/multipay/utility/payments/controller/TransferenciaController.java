package cl.multipay.utility.payments.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.TefGetOrderStatusResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.TransferenciaPayment;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.exception.UnauthorizedException;
import cl.multipay.utility.payments.http.TransferenciaClient;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.EmailService;
import cl.multipay.utility.payments.service.TransferenciaPaymentService;
import cl.multipay.utility.payments.util.Properties;

@RestController
public class TransferenciaController
{
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaController.class);

	private final Properties properties;

	private final BillService billService;
	private final TransferenciaPaymentService transferenciaPaymentService;
	private final EmailService emailService;

	private final TransferenciaClient transferenciaClient;

	public TransferenciaController(final Properties properties,
		final BillService billService, final TransferenciaPaymentService transferenciaPaymentService,
		final TransferenciaClient transferenciaClient, final EmailService emailService)
	{
		this.properties = properties;
		this.billService = billService;
		this.transferenciaPaymentService = transferenciaPaymentService;
		this.transferenciaClient = transferenciaClient;
		this.emailService = emailService;
	}

	/**
	 * Notify payment.
	 *
	 * @param id
	 * @param notifyId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST , path = "/v1/payments/transferencia/notify/{id}/{notifyId}",
		produces = MediaType.TEXT_XML_VALUE, consumes = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<String> transferenciaNotify(
		final HttpServletRequest request,
		@PathVariable("id") final String id,
		@PathVariable("notifyId") final String notifyId,
		@RequestHeader(value = "Authorization") final String auth)
	{
		logger.info("-> " + request.getRequestURL());
		try {
			// get ids
			final String tefId = getPublicId(id).orElseThrow(ServerErrorException::new);
			final String tefNotifyId = getPublicId(notifyId).orElseThrow(ServerErrorException::new);

			// check credentials
			validCredentials(auth).orElseThrow(UnauthorizedException::new);

			// get payment and bill
			final TransferenciaPayment transferenciaPayment = transferenciaPaymentService.getPendingByPublicIdAndNotifyId(tefId, tefNotifyId)
					.orElseThrow(NotFoundException::new);
			final Bill bill = billService.getWaitingById(transferenciaPayment.getBillId()).orElseThrow(NotFoundException::new);

			// get remote status
			final TefGetOrderStatusResponse tefGetOrderStatusResponse = transferenciaClient.getOrderStatus(transferenciaPayment)
					.orElseThrow(ServerErrorException::new);

			switch (tefGetOrderStatusResponse.getOrderStatus()) {
			case 101: case 106: case 107: case 109:
				// pay bill
				// ... TODO
				// if pay fail refund

				// update status
				transferenciaPayment.setStatus(TransferenciaPayment.PAID);
				bill.setStatus(Bill.SUCCEED);
				transferenciaPaymentService.save(transferenciaPayment);
				billService.save(bill);

				// send receipt
				emailService.utilityPaymentReceipt();

				// return ok
				return ResponseEntity.ok(notifyResponse());
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * Return url.
	 *
	 * @param id
	 * @return
	 */
	// TODO revisar flujo
	@GetMapping("/v1/payments/transferencia/return/{id}")
	public ResponseEntity<?> transferenciaReturn(final HttpServletRequest request, @PathVariable("id") final String id)
	{
		logger.info("-> " + request.getRequestURL());
		Long buyOrder = null;
		try {
			// get bill and payment
			final String tefId = getPublicId(id).orElseThrow(ServerErrorException::new);
			final TransferenciaPayment transferenciaPayment = transferenciaPaymentService.findByPublicId(tefId).orElseThrow(NotFoundException::new);
			final Bill bill = billService.findById(transferenciaPayment.getBillId()).orElseThrow(NotFoundException::new);
			buyOrder = bill.getBuyOrder();

			// get remote status
			final TefGetOrderStatusResponse tefGetOrderStatusResponse = transferenciaClient.getOrderStatus(transferenciaPayment)
					.orElseThrow(ServerErrorException::new);

			switch (tefGetOrderStatusResponse.getOrderStatus()) {

			// pending, paid, notified_mc, notified_ecom, notified_con
			case 100: case 101: case 106: case 107: case 109:
				return redirectEntity(properties.getTransferenciaRedirectFinal().replaceAll("\\{id\\}", bill.getPublicId()));

			// nullified, canceled_user, expired, canceled_ecom
			case 103: case 105: case 110: case 111:
				transferenciaPayment.setStatus(TransferenciaPayment.CANCELED);
				bill.setStatus(Bill.FAILED);
				transferenciaPaymentService.save(transferenciaPayment).orElseThrow(ServerErrorException::new);;
				billService.save(bill).orElseThrow(ServerErrorException::new);;
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}

		// redirect to error page
		return redirectEntity(getRedirectErrorUrl(buyOrder));
	}

	private ResponseEntity<?> redirectEntity(final String url)
	{
		logger.info("<- " + url);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url).build();
	}

	private Optional<String> getPublicId(final String id)
	{
		if ((id != null) && id.matches("[0-9a-f]{32}")) {
			return Optional.ofNullable(id);
		}
		return Optional.empty();
	}

	private String getRedirectErrorUrl(final Long buyOrder)
	{
		if ((buyOrder != null) && (buyOrder.compareTo(0L) > 0)) {
			return properties.getTransferenciaRedirectErrorOrder().replaceAll("\\{order\\}", buyOrder.toString());
		}
		return properties.getTransferenciaRedirectError();
	}

	private String notifyResponse()
	{
		return "<?xml version='1.0' encoding='UTF-8'?>" +
				"<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
					"<S:Body>" +
						"<ns2:notifyPaymentResponse xmlns:ns2=\"http://www.example.cl/ecommerce/\"/>" +
					"</S:Body>" +
				"</S:Envelope>";
	}

	private Optional<Boolean> validCredentials(final String authHeader)
	{
		if (authHeader != null) {
			if (authHeader.equals("Basic " + properties.getTransferenciaNotifyBasicAuth())) {
				return Optional.of(true);
			}
		}
		return Optional.empty();
	}
}
