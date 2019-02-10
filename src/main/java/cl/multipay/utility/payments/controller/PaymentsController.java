package cl.multipay.utility.payments.controller;

import java.util.Optional;

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
import cl.multipay.utility.payments.exception.NotFoundException;
import cl.multipay.utility.payments.exception.ServerErrorException;
import cl.multipay.utility.payments.service.BillService;
import cl.multipay.utility.payments.service.PaymentService;
import cl.multipay.utility.payments.service.WebpayService;
import cl.multipay.utility.payments.util.Properties;

@RestController
public class PaymentsController
{
	private static final Logger logger = LoggerFactory.getLogger(PaymentsController.class);

	private final Properties pr;
	private final WebpayService ws;
	private final PaymentService ps;
	private final BillService bs;

	public PaymentsController(final Properties properties, final WebpayService webpayService,
		final PaymentService paymentService, final BillService billService)
	{
		this.pr = properties;
		this.ws = webpayService;
		this.ps = paymentService;
		this.bs = billService;
	}

	@PostMapping("/v1/payments/webpay/return")
	public ResponseEntity<?> webpayReturn(@RequestParam("token_ws") final String tokenWs)
	{
		try {
			// get bill and payment
			final String token = getToken(tokenWs).orElseThrow(ServerErrorException::new);
			final WebpayPayment webpayPayment = ps.getByTokenAndStatus(token, WebpayPayment.STATUS_PENDING).orElseThrow(NotFoundException::new);
			final Bill bill = bs.findByIdAndStatus(webpayPayment.getBillId(), Bill.WAITING).orElseThrow(NotFoundException::new);

			// webpay get result
			final WebpayResultResponse webpayResultResponse = ws.result(webpayPayment).orElseThrow(ServerErrorException::new);
			webpayPayment.setResponseCode(webpayResultResponse.getDetailResponseCode());
			webpayPayment.setAuthCode(webpayResultResponse.getDetailAuthorizationCode());
			webpayPayment.setCard(webpayResultResponse.getCardNumber());
			webpayPayment.setPaymentType(webpayResultResponse.getDetailPaymentTypeCode());
			webpayPayment.setShares(webpayResultResponse.getDetailSharesNumber());
			webpayPayment.setStatus(WebpayPayment.STATUS_RESULT);
			ps.save(webpayPayment).orElseThrow(ServerErrorException::new);

			// check amount
			if (bill.getAmount().compareTo(webpayResultResponse.getDetailAmount().longValue()) != 0) {
				throw new ServerErrorException();
			}

			// ack
			ws.ack(webpayPayment).orElseThrow(ServerErrorException::new);
			webpayPayment.setStatus(WebpayPayment.STATUS_ACK);
			ps.save(webpayPayment).orElseThrow(ServerErrorException::new);

			// pay bill
			// ...

			// send receipt
			// ...

			// close payment
			// ...

			// close bill
			// ...

			// redirect to webpay
			return ResponseEntity.status(HttpStatus.OK).contentType(MediaType.TEXT_HTML)
					.body(redirectForm(webpayResultResponse.getUrlRedirection(), token));

		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}

		final String redirectUrl = pr.getWebpayRedirectError();
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
	}

	@PostMapping("/v1/payments/webpay/final")
	public void webpayFinal()
	{

	}

	private Optional<String> getToken(final String token)
	{
		if ((token != null) && token.matches("[0-9a-f]{64}")) {
			return Optional.of(token);
		}
		return Optional.empty();
	}

	private String redirectForm(final String url, final String token)
	{
		return  "<div style=\"background-color: #F7F7F7;position: fixed;top: 0;bottom: 0;left: 0;right: 0;z-index: 9999;\"></div>" +
				"<form id=\"redirectionForm\" method=\"post\" action=\""+url+"\">" +
				"  <input type=\"hidden\" name=\"token_ws\" value=\""+token+"\" />" +
				"</form>" +
				"<script>document.getElementById('redirectionForm').submit();</script>";
	}
}
