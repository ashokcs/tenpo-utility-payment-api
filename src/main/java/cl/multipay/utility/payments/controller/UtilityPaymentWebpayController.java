package cl.multipay.utility.payments.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.MulticajaPayBillResponse;
import cl.multipay.utility.payments.dto.WebpayResultResponse;
import cl.multipay.utility.payments.entity.UtilityPaymentBill;
import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;
import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;
import cl.multipay.utility.payments.event.SendReceiptWebpayEvent;
import cl.multipay.utility.payments.event.TotaliserEvent;
import cl.multipay.utility.payments.exception.HttpException;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.http.UtilityPaymentClient;
import cl.multipay.utility.payments.http.WebpayClient;
import cl.multipay.utility.payments.service.UtilityPaymentBillService;
import cl.multipay.utility.payments.service.UtilityPaymentTransactionService;
import cl.multipay.utility.payments.service.UtilityPaymentWebpayService;
import cl.multipay.utility.payments.util.Properties;
import cl.multipay.utility.payments.util.Utils;

@RestController
public class UtilityPaymentWebpayController
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentWebpayController.class);

	private final Utils utils;
	private final Properties properties;
	private final UtilityPaymentTransactionService utilityPaymentTransactionService;
	private final UtilityPaymentBillService utilityPaymentBillService;
	private final UtilityPaymentWebpayService utilityPaymentWebpayService;

	private final WebpayClient webpayClient;
	private final UtilityPaymentClient utilityPaymentClient;
	private final ApplicationEventPublisher applicationEventPublisher;

	public UtilityPaymentWebpayController(final Utils utils, final Properties properties,
		final UtilityPaymentTransactionService utilityPaymentTransactionService,
		final UtilityPaymentBillService utilityPaymentBillService,
		final UtilityPaymentWebpayService utilityPaymentWebpayService,
		final WebpayClient webpayClient, final UtilityPaymentClient utilityPaymentClient,
		final ApplicationEventPublisher applicationEventPublisher)
	{
		this.utils = utils;
		this.properties = properties;
		this.utilityPaymentTransactionService = utilityPaymentTransactionService;
		this.utilityPaymentBillService = utilityPaymentBillService;
		this.utilityPaymentWebpayService = utilityPaymentWebpayService;
		this.webpayClient = webpayClient;
		this.utilityPaymentClient = utilityPaymentClient;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@PostMapping("/v1/payments/webpay/return")
	public ResponseEntity<?> webpayReturn(
		final HttpServletRequest request,
		@RequestParam(required = false, name = "token_ws") final String tokenWs,
		@RequestParam(required = false, name = "buy_order") Long utilityPaymentBuyOrder
	) {
		logger.info("-> " + request.getRequestURL() + "?token_ws=" + tokenWs);
		try {
			// get utility payment transaction and webpay
			final String token = getToken(tokenWs).orElseThrow(ServerErrorException::new);
			final UtilityPaymentWebpay utilityPaymentWebpay = utilityPaymentWebpayService.getPendingByToken(token).orElseThrow(NotFoundException::new);
			final UtilityPaymentTransaction utilityPaymentTransaction = utilityPaymentTransactionService.getWaitingById(utilityPaymentWebpay.getTransactionId()).orElseThrow(NotFoundException::new);
			final UtilityPaymentBill utilityPaymentBill = utilityPaymentBillService.getPendingByTransactionId(utilityPaymentWebpay.getTransactionId()).orElseThrow(NotFoundException::new);
			utilityPaymentBuyOrder = utilityPaymentTransaction.getBuyOrder();
			MDC.put("transaction", utils.mdc(utilityPaymentTransaction.getPublicId()));

			// webpay get result
			final WebpayResultResponse webpayResultResponse = webpayClient.result(utilityPaymentWebpay).orElseThrow(ServerErrorException::new);
			utilityPaymentWebpay.setResponseCode(webpayResultResponse.getDetailResponseCode());
			utilityPaymentWebpay.setAuthCode(webpayResultResponse.getDetailAuthorizationCode());
			utilityPaymentWebpay.setCard(webpayResultResponse.getCardNumber());
			utilityPaymentWebpay.setPaymentType(webpayResultResponse.getDetailPaymentTypeCode());
			utilityPaymentWebpay.setShares(webpayResultResponse.getDetailSharesNumber());
			utilityPaymentWebpay.setStatus(UtilityPaymentWebpay.RESULTED);
			utilityPaymentWebpayService.save(utilityPaymentWebpay).orElseThrow(ServerErrorException::new);

			// if payment approved
			if (isWebpayPaymentApproved(webpayResultResponse) == true) {

				final Long debtDataId = utilityPaymentBill.getDataId();
				final Integer debtNumber = utilityPaymentBill.getNumber();
				final Long amount = utilityPaymentBill.getAmount();
				final Optional<MulticajaPayBillResponse> billPayment = utilityPaymentClient.payBill(debtDataId, debtNumber, amount);

				if (billPayment.isPresent()) {

					// update bill // TODO auth code, mc_code
					utilityPaymentBill.setStatus(UtilityPaymentBill.CONFIRMED);
					utilityPaymentBillService.save(utilityPaymentBill);

					// update transaction
					utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.SUCCEEDED);
					utilityPaymentTransactionService.save(utilityPaymentTransaction);

					// publish send receipt
					applicationEventPublisher.publishEvent(new SendReceiptWebpayEvent(utilityPaymentTransaction, utilityPaymentBill, utilityPaymentWebpay));

					// publish add totaliser data
					applicationEventPublisher.publishEvent(new TotaliserEvent(utilityPaymentTransaction.getAmount()));

				} else {
					// update bill // TODO mc_code
					utilityPaymentBill.setStatus(UtilityPaymentBill.FAILED);
					utilityPaymentBillService.save(utilityPaymentBill);

					// update transaction
					utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.FAILED);
					utilityPaymentTransactionService.save(utilityPaymentTransaction);

					// throw exception
					throw new ServerErrorException();
				}
			}

			// if payment not approved
			if (isWebpayPaymentApproved(webpayResultResponse) == false) {
				utilityPaymentTransaction.setStatus(UtilityPaymentTransaction.FAILED);
				utilityPaymentTransactionService.save(utilityPaymentTransaction).orElseThrow(ServerErrorException::new);
			}

			// webpay ack
			webpayClient.ack(utilityPaymentWebpay).orElseThrow(ServerErrorException::new);
			utilityPaymentWebpay.setStatus(UtilityPaymentWebpay.ACKNOWLEDGED);
			utilityPaymentWebpayService.save(utilityPaymentWebpay);

			// redirect to webpay
			return postRedirectEntity(webpayResultResponse.getUrlRedirection(), token);

		} catch (final HttpException e) {
			logger.error(e.getClass().getName());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}

		// redirect to error page
		return redirectEntity(getRedirectErrorUrl(utilityPaymentBuyOrder));
	}

	@PostMapping("/v1/payments/webpay/final")
	public ResponseEntity<?> webpayFinal(
		final HttpServletRequest request,
		@RequestParam(required = false, name = "token_ws") final String tokenWs,
		@RequestParam(required = false, name = "TBK_TOKEN") final String tbkToken,
		@RequestParam(required = false, name = "TBK_ORDEN_COMPRA") final String tbkOrdenCompra
	) {
		logger.info("-> " + request.getRequestURL() + "?token_ws=" + tokenWs);
		try {
			// process token_ws (approved, denied)
			if ((tokenWs != null) && tokenWs.matches("[0-9a-f]{64}")) {
				final UtilityPaymentWebpay utilityPaymentWebpay = utilityPaymentWebpayService.getAckByToken(tokenWs)
						.orElseThrow(NotFoundException::new);
				final UtilityPaymentTransaction utilityPaymentTransaction = utilityPaymentTransactionService.findById(utilityPaymentWebpay.getTransactionId())
						.orElseThrow(NotFoundException::new);

				if (UtilityPaymentTransaction.SUCCEEDED.equals(utilityPaymentTransaction.getStatus())) {
					return redirectEntity(properties.webpayFrontFinal.replaceAll("\\{id\\}", utilityPaymentTransaction.getPublicId()));
				} else {
					return redirectEntity(properties.webpayFrontErrorOrder.replaceAll("\\{order\\}", utilityPaymentTransaction.getBuyOrder().toString()));
				}
			}

			// process tbk_token (user cancellation)
			if ((tbkToken != null) && tbkToken.matches("[0-9a-f]{64}")) {
				return postRedirectEntity(properties.webpayReturnUrl, tbkToken);
			}

			// process tbk_orden_compra (timeout)
			if ((tbkOrdenCompra != null) && tbkOrdenCompra.matches("[0-9]{19}")) {
				return redirectEntity(properties.webpayFrontErrorOrder.replaceAll("\\{order\\}", tbkOrdenCompra));
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return redirectEntity(properties.webpayFrontError);
	}

	private Optional<String> getToken(final String token)
	{
		if ((token != null) && token.matches("[0-9a-f]{64}")) {
			return Optional.of(token);
		}
		return Optional.empty();
	}

	private boolean isWebpayPaymentApproved(final WebpayResultResponse webpay)
	{
		if ((webpay != null) && (webpay.getDetailResponseCode() != null) && (webpay.getDetailResponseCode() == 0)) {
			return true;
		}
		return false;
	}

	private String getRedirectErrorUrl(final Long buyOrder)
	{
		if ((buyOrder != null) && (buyOrder.compareTo(0L) > 0)) {
			return properties.webpayFrontErrorOrder.replaceAll("\\{order\\}", buyOrder.toString());
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
