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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.event.SendReceiptWebpayEvent;
import cl.tenpo.utility.payments.exception.HttpException;
import cl.tenpo.utility.payments.exception.NotFoundException;
import cl.tenpo.utility.payments.exception.ServerErrorException;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.object.dto.UtilityConfirmResponse;
import cl.tenpo.utility.payments.object.dto.WebpayResultResponse;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.UtilityService;
import cl.tenpo.utility.payments.service.WebpayService;
import cl.tenpo.utility.payments.util.Properties;
import cl.tenpo.utility.payments.util.Utils;
import cl.tenpo.utility.payments.util.http.UtilityClient;
import cl.tenpo.utility.payments.util.http.WebpayClient;

/**
 * @author Carlos Izquierdo
 */
@RestController
public class WebpayController
{
	private static final Logger logger = LoggerFactory.getLogger(WebpayController.class);

	private final ApplicationEventPublisher eventPublisher;
	private final BillService billService;
	private final Properties properties;
	private final TransactionService transactionService;
	private final UtilityClient utilitiesClient;
	private final UtilityService utilityService;
	private final WebpayService webpayService;
	private final WebpayClient webpayClient;

	public WebpayController(
		final ApplicationEventPublisher eventPublisher,
		final BillService billService,
		final Properties properties,
		final TransactionService transactionService,
		final UtilityClient utilitiesClient,
		final UtilityService utilityService,
		final WebpayService webpayService,
		final WebpayClient webpayClient
	){
		this.eventPublisher = eventPublisher;
		this.billService = billService;
		this.properties = properties;
		this.transactionService = transactionService;
		this.utilitiesClient = utilitiesClient;
		this.utilityService = utilityService;
		this.webpayService = webpayService;
		this.webpayClient = webpayClient;
	}

	@PostMapping("/v1/webpay/{id}/return")
	public ResponseEntity<?> webpayReturn(
		final HttpServletRequest request,
		@PathVariable("id") final String requestId,
		@RequestParam(required = false, name = "token_ws") final String requestTokenWs
	){
		logger.info("-> " + request.getRequestURL() + "?token_ws=" + requestTokenWs);
		String transactionPublicId = null;
		try {
			// get transaction and webpay
			final String publicId = Utils.getValidParam(requestId, "[0-9a-f\\-]{36}").orElseThrow(NotFoundException::new);;
			final String tokenWs = Utils.getValidParam(requestTokenWs, "[0-9a-f]{64}").orElseThrow(NotFoundException::new);

			final Webpay webpay = webpayService.getWaitingByPublicIdAndToken(publicId, tokenWs).orElseThrow(NotFoundException::new);
			final Transaction transaction = transactionService.getWaitingById(webpay.getTransactionId()).orElseThrow(NotFoundException::new);
			final Bill bill = billService.getWaitingByTransactionId(webpay.getTransactionId()).orElseThrow(NotFoundException::new);
			final Utility utility = utilityService.findById(bill.getUtilityId()).orElseThrow(NotFoundException::new);
			bill.setUtility(utility);
			transactionPublicId = transaction.getPublicId();

			// webpay get result
			final Optional<WebpayResultResponse> webpayResultResponseOpt = webpayClient.result(webpay);
			if (!webpayResultResponseOpt.isPresent()) {
				webpay.setStatus(Webpay.FAILED_RESULT);
				webpayService.save(webpay);
				transaction.setStatus(Transaction.FAILED);
				transactionService.save(transaction);
				throw new ServerErrorException();
			}

			final WebpayResultResponse webpayResultResponse = webpayResultResponseOpt.get();
			webpay.setCode(webpayResultResponse.getDetailResponseCode());
			webpay.setAuthCode(webpayResultResponse.getDetailAuthorizationCode());
			webpay.setCardNumber(webpayResultResponse.getCardNumber());
			webpay.setPaymentType(webpayResultResponse.getDetailPaymentTypeCode());
			webpay.setSharesNumber(webpayResultResponse.getDetailSharesNumber());
			webpay.setStatus(Webpay.RESULTED);
			webpayService.save(webpay).orElseThrow(ServerErrorException::new);

			// if payment approved
			if (isWebpayPaymentApproved(webpayResultResponse) == true) {

				final Long queryId = bill.getQueryId();
				final Integer queryOrder = bill.getQueryOrder();
				final Long amount = bill.getAmount();
				final Optional<UtilityConfirmResponse> utilityConfirm = utilitiesClient.payBill(queryId, queryOrder, amount);

				if (utilityConfirm.isPresent()) {
					// update bill
					final UtilityConfirmResponse payBillResponse = utilityConfirm.get();
					bill.setConfirmId(payBillResponse.getPaymentId());
					bill.setConfirmTransactionId(payBillResponse.getMcCode());
					bill.setConfirmState(payBillResponse.getState());
					bill.setConfirmAuthCode(payBillResponse.getAuthCode());
					bill.setConfirmDate(payBillResponse.getDate());
					bill.setConfirmHour(payBillResponse.getHour());
					bill.setStatus(Bill.CONFIRMED);
					billService.save(bill);

					// update transaction
					transaction.setStatus(Transaction.SUCCEEDED);
					transactionService.save(transaction);

					// publish send receipt
					eventPublisher.publishEvent(new SendReceiptWebpayEvent(bill, transaction, webpay));

				} else {
					// update bill
					bill.setStatus(Bill.FAILED);
					billService.save(bill);

					// update transaction
					transaction.setStatus(Transaction.FAILED);
					transactionService.save(transaction);

					// throw exception
					throw new ServerErrorException();
				}
			}

			// if payment not approved
			if (isWebpayPaymentApproved(webpayResultResponse) == false) {
				transaction.setStatus(Transaction.FAILED);
				transactionService.save(transaction).orElseThrow(ServerErrorException::new);
			}

			// webpay ack
			if (webpayClient.ack(webpay).isPresent()) {
				webpay.setStatus(Webpay.ACKNOWLEDGED);
				webpayService.save(webpay);
			} else {
				webpay.setStatus(Webpay.FAILED_ACK);
				webpayService.save(webpay);
				transaction.setStatus(Transaction.FAILED);
				transactionService.save(transaction);
				throw new ServerErrorException();
			}

			// redirect to webpay
			return postRedirectEntity(webpayResultResponse.getUrlRedirection(), tokenWs);

		} catch (final HttpException e) {
			logger.error(e.getClass().getName() + ", " + e.getStackTrace()[0].getClassName() + ":" + e.getStackTrace()[0].getLineNumber());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}

		// redirect to error page
		return redirectEntity(getRedirectErrorUrl(transactionPublicId));
	}

	@PostMapping("/v1/webpay/{id}/final")
	public ResponseEntity<?> webpayFinal(
		final HttpServletRequest request,
		@PathVariable("id") final String requestId,
		@RequestParam(required = false, name = "token_ws") final String tokenWs,
		@RequestParam(required = false, name = "TBK_TOKEN") final String tbkToken,
		@RequestParam(required = false, name = "TBK_ORDEN_COMPRA") final String tbkOrdenCompra
	) {
		logger.info("-> " + request.getRequestURL() + "?token_ws=" + tokenWs);
		try {
			final String publicId = Utils.getValidParam(requestId, "[0-9a-f\\-]{36}").orElseThrow(NotFoundException::new);;

			// process token_ws (approved, denied)
			if (Utils.getValidParam(tokenWs, "[0-9a-f]{64}").isPresent()) {
				final Webpay webpay = webpayService.getAckByPublicIdAndToken(publicId, tokenWs).orElseThrow(NotFoundException::new);
				final Transaction transaction = transactionService.findById(webpay.getTransactionId()).orElseThrow(NotFoundException::new);

				if (Transaction.SUCCEEDED.equals(transaction.getStatus())) {
					return redirectEntity(properties.webpayFrontFinal.replaceAll("\\{id\\}", transaction.getPublicId()));
				} else {
					return redirectEntity(properties.webpayFrontErrorOrder.replaceAll("\\{id\\}", transaction.getPublicId()));
				}
			}

			// process tbk_token (user cancellation)
			if (Utils.getValidParam(tbkToken, "[0-9a-f]{64}").isPresent()) {
				return postRedirectEntity(properties.webpayReturnUrl.replaceAll("\\{id\\}", publicId), tbkToken);
			}

			// process tbk_orden_compra (timeout)
			/*if ((tbkOrdenCompra != null) && tbkOrdenCompra.matches("[0-9]{19}")) {
				return redirectEntity(properties.webpayFrontErrorOrder.replaceAll("\\{id\\}", tbkOrdenCompra));
			}*/
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return redirectEntity(properties.webpayFrontError);
	}

	private boolean isWebpayPaymentApproved(final WebpayResultResponse webpay)
	{
		if ((webpay != null) && (webpay.getDetailResponseCode() != null) && (webpay.getDetailResponseCode() == 0)) {
			return true;
		}
		return false;
	}

	private String getRedirectErrorUrl(final String transactionPublicId)
	{
		if ((transactionPublicId != null) && !transactionPublicId.isEmpty()) {
			return properties.webpayFrontErrorOrder.replaceAll("\\{id\\}", transactionPublicId);
		}
		return properties.webpayFrontError;
	}

	private ResponseEntity<?> redirectEntity(final String url)
	{
		logger.info("<- " + url);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, url).build();
	}

	private ResponseEntity<String> postRedirectEntity(final String url, final String token)
	{
		logger.info("<- " + url + "?token_ws=" + token);
		final String body = "<div style=\"background-color: #F7F7F7;position: fixed;top: 0;bottom: 0;left: 0;right: 0;z-index: 9999;\"></div>" +
				"<form id=\"redirectionForm\" method=\"post\" action=\""+url+"\">" +
				"  <input type=\"hidden\" name=\"token_ws\" value=\""+token+"\" />" +
				"</form>" +
				"<script>document.getElementById('redirectionForm').submit();</script>";
		return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_HTML).body(body);
	}
}
