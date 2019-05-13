package cl.tenpo.utility.payments.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Job;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.object.dto.TransferenciaStatusResponse;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.JobService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.TransferenciaService;
import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.util.Properties;
import cl.tenpo.utility.payments.util.http.TransferenciaClient;

/**
 * @author Carlos Izquierdo
 */
@RestController
public class TransferenciaController
{
	private static final Logger logger = LoggerFactory.getLogger(TransferenciaController.class);

	private final BillService billService;
	private final JobService jobService;
	private final Properties properties;
	private final TransactionService transactionService;
	private final TransactionTemplate transactionTemplate;
	private final TransferenciaService transferenciaService;
	private final TransferenciaClient transferenciaClient;

	public TransferenciaController(
		final BillService billService,
		final JobService jobService,
		final PlatformTransactionManager transactionManager,
		final Properties properties,
		final TransactionService transactionService,
		final TransferenciaService transferenciaService,
		final TransferenciaClient transferenciaClient
	){
		this.billService = billService;
		this.jobService = jobService;
		this.properties = properties;
		this.transactionService = transactionService;
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		this.transferenciaService = transferenciaService;
		this.transferenciaClient = transferenciaClient;
	}

	@GetMapping("/v1/transferencia/{id:[0-9a-f\\-]{36}}/return")
	public ResponseEntity<Object> tefReturn(
		final HttpServletRequest request,
		@PathVariable("id") final String requestId
	){
		logger.info("-> " + request.getRequestURL());
		String transactionPublicId = null;
		try {
			// get transferencia, transaction
			final Transferencia transferencia = transferenciaService.findByPublicId(requestId).orElseThrow(Http::TransferenciaNotFound);
			final Transaction transaction = transactionService.findById(transferencia.getTransactionId()).orElseThrow(Http::TransactionNotFound);
			transactionPublicId = transaction.getPublicId();

			// get transferencia remote status
			final Optional<TransferenciaStatusResponse> tsrOpt = transferenciaClient.getOrderStatus(transferencia);

			if (tsrOpt.isPresent()) {
				final TransferenciaStatusResponse tsr = tsrOpt.get();
				switch (tsr.getOrderStatus()) {

				// pending, paid, notified_mc, notified_ecom, notified_con
				case 100: case 101: case 106: case 107: case 109:
					return redirectEntity(properties.transferenciaFrontFinal.replaceAll("\\{id\\}", transaction.getPublicId()));

				// nullified, canceled_user, expired, canceled_ecom
				case 103: case 105: case 110: case 111:
					transferencia.setStatus(Transferencia.CANCELED);
					transferenciaService.save(transferencia);
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
			// check credentials
			checkCredentials(auth).orElseThrow(Http::Unauthorized);

			// get resources
			final Transferencia transferencia = transferenciaService.getWaitingOrFailedByPublicIdAndNotifyId(requestId, requestNotifyId).orElseThrow(Http::TransferenciaNotFound);
			final Transaction transaction = transactionService.getWaitingById(transferencia.getTransactionId()).orElseThrow(Http::TransactionNotFound);
			final List<Bill> bills = billService.getWaitingByTransactionId(transferencia.getTransactionId());

			// sanity checks
			if (bills == null || bills.size() <= 0) throw Http.BillNotFound();
			if (bills.stream().mapToLong(b -> b.getAmount()).sum() != transaction.getAmount().longValue()) throw Http.BillNotFound();

			// get transferencia remote status
			final TransferenciaStatusResponse tsr = transferenciaClient.getOrderStatus(transferencia).orElseThrow(Http::ServerError);
			switch (tsr.getOrderStatus()) {

			case 101: case 106: case 107: case 109:

				transactionTemplate.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(final TransactionStatus status) {
						// update transferencia
						transferencia.setStatus(Transferencia.PAID);
						transferenciaService.save(transferencia).orElseThrow(Http::ServerError);

						// update transaction
						transaction.setStatus(Transaction.PROCESSING);
						transactionService.save(transaction).orElseThrow(Http::ServerError);

						// update bills
						for (final Bill bill : bills) {
							bill.setStatus(Bill.PROCESSING);
							billService.save(bill).orElseThrow(Http::ServerError);
						}

						// save job
						final Job job = new Job();
						job.setStatus(Job.RUNNING);
						job.setTransactionId(transaction.getId());
						jobService.save(job).orElseThrow(Http::ServerError);

						// send receipt
						// ... TODO
					}
				});

				// return ok
				return ResponseEntity.ok(notifyResponse(request));
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	private ResponseEntity<Object> redirectEntity(final String url)
	{
		logger.info("<- " + url);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url).build();
	}

	private String getRedirectErrorUrl(final String transactionPublicId)
	{
		if ((transactionPublicId != null) && (!transactionPublicId.isEmpty())) {
			return properties.transferenciaFrontErrorOrder.replaceAll("\\{id\\}", transactionPublicId);
		}
		return properties.transferenciaFrontError;
	}

	private String notifyResponse(final HttpServletRequest request)
	{
		logger.info("<- " + request.getRequestURL() + " [OK]");
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
			if (authHeader.equals("Basic " + properties.transferenciaNotifyBasicAuth)) {
				return Optional.of(true);
			}
		}
		return Optional.empty();
	}
}
