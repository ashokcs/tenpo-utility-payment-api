package cl.tenpo.utility.payments.controller;

import javax.validation.Valid;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.dto.ReceiptRequest;
import cl.tenpo.utility.payments.dto.TransactionRequest;
import cl.tenpo.utility.payments.dto.TransferenciaOrderResponse;
import cl.tenpo.utility.payments.dto.WebpayInitResponse;
import cl.tenpo.utility.payments.event.SendReceipTransferenciaEvent;
import cl.tenpo.utility.payments.event.SendReceiptWebpayEvent;
import cl.tenpo.utility.payments.exception.BadRequestException;
import cl.tenpo.utility.payments.exception.NotFoundException;
import cl.tenpo.utility.payments.exception.ServerErrorException;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.TransferenciaService;
import cl.tenpo.utility.payments.service.WebpayService;
import cl.tenpo.utility.payments.util.Utils;
import cl.tenpo.utility.payments.util.http.RecaptchaClient;
import cl.tenpo.utility.payments.util.http.TransferenciaClient;
import cl.tenpo.utility.payments.util.http.WebpayClient;

/**
 * @author Carlos Izquierdo
 */
@RestController
@RequestMapping(
	produces = MediaType.APPLICATION_JSON_VALUE
)
public class TransactionsController
{
	private final ApplicationEventPublisher eventPublisher;
	private final BillService billService;
	private final RecaptchaClient recaptchaClient;
	private final TransactionService transactionService;
	private final TransferenciaClient transferenciaClient;
	private final TransferenciaService transferenciaService;
	private final WebpayClient webpayClient;
	private final WebpayService webpayService;

	public TransactionsController(
		final ApplicationEventPublisher eventPublisher,
		final BillService billService,
		final RecaptchaClient recaptchaClient,
		final TransactionService transactionService,
		final TransferenciaClient transferenciaClient,
		final TransferenciaService transferenciaService,
		final WebpayService webpayService,
		final WebpayClient webpayClient
	){
		this.eventPublisher = eventPublisher;
		this.billService = billService;
		this.recaptchaClient = recaptchaClient;
		this.transactionService = transactionService;
		this.transferenciaClient = transferenciaClient;
		this.transferenciaService = transferenciaService;
		this.webpayService = webpayService;
		this.webpayClient = webpayClient;
	}

	@Transactional
	@RequestMapping(
		path = "/v1/transactions",
		method = RequestMethod.POST,
		consumes = MediaType.APPLICATION_JSON_VALUE
	)
	public Transaction create(
		@RequestBody @Valid final TransactionRequest request
	){
		// get bill by public_id
		final Bill bill = billService.findWaitingByPublicId(request.getBill()).orElseThrow(NotFoundException::new);

		// create transaction
		final String id = Utils.uuid();
		final String status = Transaction.WAITING;
		final String paymentMethod = Utils.getPaymentMethodName(request.getPaymentMethod());
		final Long amount = bill.getAmount();
		final String email = request.getEmail();

		final Transaction transaction = new Transaction();
		transaction.setStatus(status);
		transaction.setPublicId(id);
		transaction.setPaymentMethod(paymentMethod);
		transaction.setAmount(amount);
		transaction.setEmail(email);
		transactionService.saveAndRefresh(transaction).orElseThrow(ServerErrorException::new);

		// update bills
		bill.setTransactionId(transaction.getId());
		billService.save(bill).orElseThrow(ServerErrorException::new);
		transaction.setBill(bill);

		// webpay payment method
		if (Transaction.WEBPAY.equals(transaction.getPaymentMethod())) {
			final String webpayPublicId = Utils.uuid();
			final WebpayInitResponse wir = webpayClient.init(transaction, webpayPublicId).orElseThrow(ServerErrorException::new);
			final Webpay webpay = new Webpay();
			webpay.setStatus(Webpay.WAITING);
			webpay.setPublicId(webpayPublicId);
			webpay.setTransactionId(transaction.getId());
			webpay.setUrl(wir.getUrl());
			webpay.setToken(wir.getToken());
			webpayService.save(webpay).orElseThrow(ServerErrorException::new);
			transaction.setWebpay(webpay);
		}

		// transferencia payment method
		if (Transaction.TRANSFERENCIA.equals(transaction.getPaymentMethod())) {
			final String tefPublicId = Utils.uuid();
			final String tefNotifyId = Utils.uuid();
			final TransferenciaOrderResponse tefOrder = transferenciaClient.createOrder(transaction, tefPublicId, tefNotifyId).orElseThrow(ServerErrorException::new);
			final Transferencia transferencia = new Transferencia();
			transferencia.setStatus(Transferencia.WAITING);
			transferencia.setPublicId(tefPublicId);
			transferencia.setTransactionId(transaction.getId());
			transferencia.setNotifyId(tefNotifyId);
			transferencia.setUrl(tefOrder.getRedirectUrl());
			transferencia.setOrder(tefOrder.getMcOrderId());
			transferenciaService.save(transferencia).orElseThrow(ServerErrorException::new);
			transaction.setTransferencia(transferencia);
		}

		return transaction;
	}

	@GetMapping("/v1/transactions/{id:[0-9a-f\\-]{36}}")
	public Transaction get(@PathVariable("id") final String publicId)
	{
		final Transaction transaction = transactionService.findByPublicId(publicId).orElseThrow(NotFoundException::new);
		transaction.setBill(billService.findByTransactionId(transaction.getId()).orElse(null));

		if (transaction.getPaymentMethod().equals(Transaction.WEBPAY)) {
			transaction.setWebpay(webpayService.findByTransactionId(transaction.getId()).orElse(null));
		} else if (transaction.getPaymentMethod().equals(Transaction.TRANSFERENCIA)) {
			transaction.setTransferencia(transferenciaService.findByTransactionId(transaction.getId()).orElse(null));
		}
		return transaction;
	}

	@PostMapping("/v1/transactions/{id:[0-9a-f\\-]{36}}/receipt")
	public void receipt(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final ReceiptRequest receiptRequest
	) {
		// check captcha
		recaptchaClient.check(receiptRequest.getRecaptcha()).orElseThrow(BadRequestException::new);

		// get transaction by id and status
		final Transaction transaction = transactionService.getSucceedByPublicId(publicId).orElseThrow(NotFoundException::new);
		final Bill bill = billService.findByTransactionId(transaction.getId()).orElseThrow(NotFoundException::new);
		transaction.setEmail(receiptRequest.getEmail());

		if (transaction.getPaymentMethod().equals(Transaction.WEBPAY)) {
			final Webpay webpay = webpayService.findByTransactionId(transaction.getId()).orElseThrow(NotFoundException::new);
			eventPublisher.publishEvent(new SendReceiptWebpayEvent(bill, transaction, webpay));

		} else if (transaction.getPaymentMethod().equals(Transaction.TRANSFERENCIA)) {
			final Transferencia transferencia = transferenciaService.findByTransactionId(transaction.getId()).orElseThrow(NotFoundException::new);
			eventPublisher.publishEvent(new SendReceipTransferenciaEvent(bill, transaction, transferencia));
		}
	}
}
