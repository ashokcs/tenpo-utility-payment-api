package cl.tenpo.utility.payments.controller;

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

import cl.tenpo.utility.payments.event.SendReceipTransferenciaEvent;
import cl.tenpo.utility.payments.exception.NotFoundException;
import cl.tenpo.utility.payments.exception.ServerErrorException;
import cl.tenpo.utility.payments.exception.UnauthorizedException;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.object.dto.TransferenciaStatusResponse;
import cl.tenpo.utility.payments.object.dto.UtilityConfirmResponse;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.TransferenciaService;
import cl.tenpo.utility.payments.service.UtilityService;
import cl.tenpo.utility.payments.util.Properties;
import cl.tenpo.utility.payments.util.Utils;
import cl.tenpo.utility.payments.util.http.TransferenciaClient;
import cl.tenpo.utility.payments.util.http.UtilityClient;

/**
 * @author Carlos Izquierdo
 */
@RestController
public class TransferenciaController
{
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaController.class);

	private final ApplicationEventPublisher applicationEventPublisher;
	private final BillService billService;
	private final Properties properties;
	private final TransactionService transactionService;
	private final TransferenciaService transferenciaService;
	private final TransferenciaClient transferenciaClient;
	private final UtilityClient utilititesClient;
	private final UtilityService utilityService;

	public TransferenciaController(
		final ApplicationEventPublisher applicationEventPublisher,
		final BillService billService,
		final Properties properties,
		final TransactionService transactionService,
		final TransferenciaService transferenciaService,
		final TransferenciaClient transferenciaClient,
		final UtilityClient utilitiesClient,
		final UtilityService utilityService
	){
		this.applicationEventPublisher = applicationEventPublisher;
		this.billService = billService;
		this.properties = properties;
		this.transactionService = transactionService;
		this.transferenciaService = transferenciaService;
		this.transferenciaClient = transferenciaClient;
		this.utilititesClient = utilitiesClient;
		this.utilityService = utilityService;
	}

	/**
	 * Notify payment.
	 *
	 * @param requestId
	 * @param requestNotifyId
	 * @return
	 */
	@RequestMapping(
		method = RequestMethod.POST,
		path = "/v1/transferencia/{id:[0-9a-f\\-]{36}}/notify/{notifyId:[0-9a-f\\-]{36}}",
		consumes = MediaType.TEXT_XML_VALUE,
		produces = MediaType.TEXT_XML_VALUE
	)
	public ResponseEntity<String> notify(
		final HttpServletRequest request,
		@PathVariable("id") final String requestId,
		@PathVariable("notifyId") final String requestNotifyId,
		@RequestHeader(value = "Authorization") final String auth
	){
		logger.info("-> " + request.getRequestURL());
		try {
			// get and check ids
			final String id = Utils.getValidParam(requestId, "[0-9a-f\\-]{36}").orElseThrow(NotFoundException::new);
			final String notifyId = Utils.getValidParam(requestNotifyId, "[0-9a-f\\-]{36}").orElseThrow(NotFoundException::new);

			// check credentials
			checkCredentials(auth).orElseThrow(UnauthorizedException::new);

			// get transferencia, transaction and bill
			final Transferencia transferencia = transferenciaService.getWaitingOrPaidByPublicIdAndNotifyId(id, notifyId).orElseThrow(NotFoundException::new);
			final Transaction transaction = transactionService.getWaitingById(transferencia.getTransactionId()).orElseThrow(NotFoundException::new);
			final Bill bill = billService.getWaitingByTransactionId(transferencia.getTransactionId()).orElseThrow(NotFoundException::new);
			final Utility utility = utilityService.findById(bill.getUtilityId()).orElseThrow(NotFoundException::new);
			bill.setUtility(utility);

			// get eft remote status
			final TransferenciaStatusResponse transferenciaStatusResponse = transferenciaClient.getOrderStatus(transferencia)
					.orElseThrow(ServerErrorException::new);

			switch (transferenciaStatusResponse.getOrderStatus()) {
			case 101: case 106: case 107: case 109:

				// update transferencia status
				transferencia.setStatus(Transferencia.PAID);
				transferenciaService.save(transferencia);

				// pay bill
				final Long queryId = bill.getQueryId();
				final Integer queryOrder = bill.getQueryOrder();
				final Long amount = bill.getAmount();
				final Optional<UtilityConfirmResponse> confirmResponseOpt = utilititesClient.payBill(queryId, queryOrder, amount);

				if (confirmResponseOpt.isPresent()) {
					// update bill
					final UtilityConfirmResponse confirmResponse = confirmResponseOpt.get();
					bill.setStatus(Bill.CONFIRMED);
					bill.setConfirmId(confirmResponse.getPaymentId());
					bill.setConfirmState(confirmResponse.getState());
					bill.setConfirmTransactionId(confirmResponse.getMcCode());
					bill.setConfirmAuthCode(confirmResponse.getAuthCode());
					bill.setConfirmDate(confirmResponse.getDate());
					bill.setConfirmHour(confirmResponse.getHour());
					billService.save(bill);

					// update transaction
					transaction.setStatus(Transaction.SUCCEEDED);
					transactionService.save(transaction);

					// publish send receipt
					applicationEventPublisher.publishEvent(new SendReceipTransferenciaEvent(bill, transaction, transferencia));

					// return ok
					final String response = notifyResponse();
					logger.info("<- " + request.getRequestURL() + " ["+response+"]");
					return ResponseEntity.ok(response);
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	/**
	 * Return url.
	 *
	 * @param requestId
	 * @return
	 */
	@GetMapping("/v1/transferencia/{id:[0-9a-f\\-]{36}}/return")
	public ResponseEntity<Object> tefReturn(
		final HttpServletRequest request,
		@PathVariable("id") final String requestId
	){
		logger.info("-> " + request.getRequestURL());
		String transactionPublicId = null;
		try {
			// get transferencia, transaction
			final Transferencia transferencia = transferenciaService.findByPublicId(requestId).orElseThrow(NotFoundException::new);
			final Transaction transaction = transactionService.findById(transferencia.getTransactionId()).orElseThrow(NotFoundException::new);
			transactionPublicId = transaction.getPublicId();

			// get transferencia remote status
			final Optional<TransferenciaStatusResponse> tsrOpt = transferenciaClient.getOrderStatus(transferencia);

			if (tsrOpt.isPresent()) {
				final TransferenciaStatusResponse tsr = tsrOpt.get();
				switch (tsr.getOrderStatus()) {

				// pending, paid, notified_mc, notified_ecom, notified_con
				case 100: case 101: case 106: case 107: case 109:
					return redirectEntity(properties.eftFrontFinal.replaceAll("\\{id\\}", transaction.getPublicId()));

				// nullified, canceled_user, expired, canceled_ecom
				case 103: case 105: case 110: case 111:
					transferencia.setStatus(Transferencia.CANCELED);
					transferenciaService.save(transferencia).orElseThrow(ServerErrorException::new);;
					transaction.setStatus(Transaction.FAILED);
					transactionService.save(transaction);
				}
			} else {
				transferencia.setStatus(Transferencia.FAILED);
				transferenciaService.save(transferencia);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}

		// redirect to error page
		return redirectEntity(getRedirectErrorUrl(transactionPublicId));
	}

	private ResponseEntity<Object> redirectEntity(final String url)
	{
		logger.info("<- " + url);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url).build();
	}

	private String getRedirectErrorUrl(final String transactionPublicId)
	{
		if ((transactionPublicId != null) && (!transactionPublicId.isEmpty())) {
			return properties.eftFrontErrorOrder.replaceAll("\\{id\\}", transactionPublicId);
		}
		return properties.eftFrontError;
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

	private Optional<Boolean> checkCredentials(final String authHeader)
	{
		if (authHeader != null) {
			if (authHeader.equals("Basic " + properties.eftNotifyBasicAuth)) {
				return Optional.of(true);
			}
		}
		return Optional.empty();
	}
}
