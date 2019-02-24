package cl.multipay.utility.payments.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
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
import cl.multipay.utility.payments.entity.UtilityPaymentEft;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.event.SendReceiptEvent;
import cl.multipay.utility.payments.event.TotaliserEvent;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.exception.UnauthorizedException;
import cl.multipay.utility.payments.http.EftClient;
import cl.multipay.utility.payments.service.UtilityPaymentBillService;
import cl.multipay.utility.payments.service.UtilityPaymentEftService;
import cl.multipay.utility.payments.service.UtilityPaymentTransactionService;
import cl.multipay.utility.payments.util.Properties;

@RestController
public class UtilityPaymentEftController
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentEftController.class);

	private final Properties properties;

	private final UtilityPaymentTransactionService utilityPaymentTransactionService;
	private final UtilityPaymentBillService utilityPaymentBillService;
	private final UtilityPaymentEftService utilityPaymentEftService;

	private final EftClient eftClient;
	private final ApplicationEventPublisher applicationEventPublisher;

	public UtilityPaymentEftController(final Properties properties,
		final UtilityPaymentTransactionService utilityPaymentTransactionService,
		final UtilityPaymentBillService utilityPaymentBillService,
		final UtilityPaymentEftService utilityPaymentEftService,
		final EftClient eftClient, final EftClient transferenciaClient,
		final ApplicationEventPublisher applicationEventPublisher)
	{
		this.properties = properties;
		this.utilityPaymentTransactionService = utilityPaymentTransactionService;
		this.utilityPaymentBillService = utilityPaymentBillService;
		this.utilityPaymentEftService = utilityPaymentEftService;
		this.eftClient = eftClient;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	/**
	 * Notify payment.
	 *
	 * @param id
	 * @param notifyId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST , path = "/v1/payments/eft/notify/{id:[0-9a-f]{32}}/{notifyId:[0-9a-f]{32}}",
		produces = MediaType.TEXT_XML_VALUE, consumes = MediaType.TEXT_XML_VALUE)
	public ResponseEntity<String> transferenciaNotify(
		final HttpServletRequest request,
		@PathVariable("id") final String id,
		@PathVariable("notifyId") final String notifyId,
		@RequestHeader(value = "Authorization") final String auth)
	{
		logger.info("-> " + request.getRequestURL());
		try {
			// get and check ids
			final String tefId = getId(id).orElseThrow(ServerErrorException::new);
			final String tefNotifyId = getId(notifyId).orElseThrow(ServerErrorException::new);

			// check credentials
			validCredentials(auth).orElseThrow(UnauthorizedException::new);

			// get utility payment eft and utility payment transaction
			final UtilityPaymentEft utilityPaymentEft = utilityPaymentEftService.getPendingByPublicIdAndNotifyId(tefId, tefNotifyId)
					.orElseThrow(NotFoundException::new);
			final UtilityPaymentTransaction utilityPaymentTransaction = utilityPaymentTransactionService.getWaitingById(utilityPaymentEft.getTransactionId())
					.orElseThrow(NotFoundException::new);

			// get eft remote status
			final TefGetOrderStatusResponse tefGetOrderStatusResponse = eftClient.getOrderStatus(utilityPaymentEft)
					.orElseThrow(ServerErrorException::new);

			switch (tefGetOrderStatusResponse.getOrderStatus()) {
			case 101: case 106: case 107: case 109:
				// pay bill
				// ... TODO
				// if bill pay fail return error for notification retry

				// update status
				utilityPaymentEft.setStatus(UtilityPaymentEft.PAID);
				utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.SUCCEEDED);
				utilityPaymentEftService.save(utilityPaymentEft);
				utilityPaymentTransactionService.save(utilityPaymentTransaction);

				// publish send receipt
				applicationEventPublisher.publishEvent(new SendReceiptEvent(utilityPaymentTransaction));

				// publish add totaliser data
				applicationEventPublisher.publishEvent(new TotaliserEvent(utilityPaymentTransaction.getAmount()));

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
	@GetMapping("/v1/payments/eft/return/{id:[0-9a-f]{32}}")
	public ResponseEntity<Object> transferenciaReturn(final HttpServletRequest request, @PathVariable("id") final String id)
	{
		logger.info("-> " + request.getRequestURL());
		Long utilityPaymentTransactionBuyOrder = null;
		try {
			// get utility payment transaction and eft
			final String tefId = getId(id).orElseThrow(ServerErrorException::new);
			final UtilityPaymentEft utilityPaymentEft = utilityPaymentEftService.findByPublicId(tefId).orElseThrow(NotFoundException::new);
			final UtilityPaymentTransaction utilityPaymentTransaction = utilityPaymentTransactionService.findById(utilityPaymentEft.getTransactionId()).orElseThrow(NotFoundException::new);
			utilityPaymentTransactionBuyOrder = utilityPaymentTransaction.getBuyOrder();

			// get eft remote status
			final TefGetOrderStatusResponse tefGetOrderStatusResponse = eftClient.getOrderStatus(utilityPaymentEft)
					.orElseThrow(ServerErrorException::new);

			switch (tefGetOrderStatusResponse.getOrderStatus()) {

			// pending, paid, notified_mc, notified_ecom, notified_con
			case 100: case 101: case 106: case 107: case 109:
				return redirectEntity(properties.eftRedirectFinal.replaceAll("\\{id\\}", utilityPaymentTransaction.getPublicId()));

			// nullified, canceled_user, expired, canceled_ecom
			case 103: case 105: case 110: case 111:
				utilityPaymentEft.setStatus(UtilityPaymentEft.CANCELED);
				utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.FAILED);
				utilityPaymentEftService.save(utilityPaymentEft).orElseThrow(ServerErrorException::new);;
				utilityPaymentTransactionService.save(utilityPaymentTransaction).orElseThrow(ServerErrorException::new);;
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}

		// redirect to error page
		return redirectEntity(getRedirectErrorUrl(utilityPaymentTransactionBuyOrder));
	}

	private ResponseEntity<Object> redirectEntity(final String url)
	{
		logger.info("<- " + url);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url).build();
	}

	private Optional<String> getId(final String id)
	{
		if ((id != null) && id.matches("[0-9a-f]{32}")) {
			return Optional.ofNullable(id);
		}
		return Optional.empty();
	}

	private String getRedirectErrorUrl(final Long buyOrder)
	{
		if ((buyOrder != null) && (buyOrder.compareTo(0L) > 0)) {
			return properties.eftRedirectErrorOrder.replaceAll("\\{order\\}", buyOrder.toString());
		}
		return properties.eftRedirectError;
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
			if (authHeader.equals("Basic " + properties.eftNotifyBasicAuth)) {
				return Optional.of(true);
			}
		}
		return Optional.empty();
	}
}
