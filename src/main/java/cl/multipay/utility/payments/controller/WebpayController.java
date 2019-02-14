package cl.multipay.utility.payments.controller;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.multipay.utility.payments.dto.WebpayResultResponse;
import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.entity.WebpayPayment;
import cl.multipay.utility.payments.exception.HttpException;
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.http.WebpayClient;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.EmailService;
import cl.multipay.utility.payments.service.WebpayPaymentService;
import cl.multipay.utility.payments.util.Properties;

@RestController
public class WebpayController
{
	private static final Logger logger = LoggerFactory.getLogger(WebpayController.class);

	private final Properties properties;
	private final WebpayPaymentService webpayPaymentService;
	private final BillService billService;
	private final EmailService emailService;

	private final WebpayClient webpayClient;

	public WebpayController(final Properties properties,
		final WebpayClient webpayClient, final WebpayPaymentService webpayPaymentService,
		final BillService billService, final EmailService emailService)
	{
		this.properties = properties;
		this.webpayClient = webpayClient;
		this.webpayPaymentService = webpayPaymentService;
		this.billService = billService;
		this.emailService = emailService;
	}

	@PostMapping("/v1/payments/webpay/return")
	public ResponseEntity<?> webpayReturn(
		final HttpServletRequest request,
		@RequestParam(required = false, name = "token_ws") final String tokenWs,
		@RequestParam(required = false, name = "buy_order") Long buyOrder
	) {
		logger.info("-> " + request.getRequestURL() + "?token_ws=" + tokenWs);
		try {
			// get bill and payment
			final String token = getToken(tokenWs).orElseThrow(ServerErrorException::new);
			final WebpayPayment webpayPayment = webpayPaymentService.getPendingByToken(token).orElseThrow(NotFoundException::new);
			final Bill bill = billService.getWaitingById(webpayPayment.getBillId()).orElseThrow(NotFoundException::new);
			buyOrder = bill.getBuyOrder();

			// webpay get result
			final WebpayResultResponse webpayResultResponse = webpayClient.result(webpayPayment).orElseThrow(ServerErrorException::new);
			webpayPayment.setResponseCode(webpayResultResponse.getDetailResponseCode());
			webpayPayment.setAuthCode(webpayResultResponse.getDetailAuthorizationCode());
			webpayPayment.setCard(webpayResultResponse.getCardNumber());
			webpayPayment.setPaymentType(webpayResultResponse.getDetailPaymentTypeCode());
			webpayPayment.setShares(webpayResultResponse.getDetailSharesNumber());
			webpayPayment.setStatus(WebpayPayment.RESULT);
			webpayPaymentService.save(webpayPayment).orElseThrow(ServerErrorException::new);

			// if payment approved
			if (isWebpayPaymentApproved(webpayResultResponse) == true) {

				// pay bill TODO
				// if pay fail, throw exception

				bill.setStatus(Bill.SUCCEED);
				billService.save(bill);

				// send receipt
				emailService.utilityPaymentReceipt();
			}

			// if payment not approved
			if (isWebpayPaymentApproved(webpayResultResponse) == false) {
				bill.setStatus(Bill.FAILED);
				billService.save(bill).orElseThrow(ServerErrorException::new);
			}

			// webpay ack
			webpayClient.ack(webpayPayment).orElseThrow(ServerErrorException::new);
			webpayPayment.setStatus(WebpayPayment.ACK);
			webpayPaymentService.save(webpayPayment);

			// redirect to webpay
			return postRedirectEntity(webpayResultResponse.getUrlRedirection(), token);

		} catch (final HttpException e) {
			logger.error(e.getClass().getName());
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}

		// redirect to error page
		return redirectEntity(getRedirectErrorUrl(buyOrder));
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
				final WebpayPayment webpayPayment = webpayPaymentService.getAckByToken(tokenWs).orElseThrow(NotFoundException::new);
				final Bill bill = billService.findById(webpayPayment.getBillId()).orElseThrow(NotFoundException::new);

				if (Bill.SUCCEED.equals(bill.getStatus())) {
					return redirectEntity(properties.getWebpayRedirectFinal().replaceAll("\\{id\\}", bill.getPublicId()));
				} else {
					return redirectEntity(properties.getWebpayRedirectErrorOrder().replaceAll("\\{order\\}", bill.getBuyOrder().toString()));
				}
			}

			// process tbk_token (user cancellation)
			if ((tbkToken != null) && tbkToken.matches("[0-9a-f]{64}")) {
				return postRedirectEntity(properties.getWebpayReturnUrl(), tbkToken);
			}

			// process tbk_orden_compra (timeout)
			if ((tbkOrdenCompra != null) && tbkOrdenCompra.matches("[0-9]{17}")) {
				return redirectEntity(properties.getWebpayRedirectErrorOrder().replaceAll("\\{order\\}", tbkOrdenCompra));
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return redirectEntity(properties.getWebpayRedirectError());
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
			return properties.getWebpayRedirectErrorOrder().replaceAll("\\{order\\}", buyOrder.toString());
		}
		return properties.getWebpayRedirectError();
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
