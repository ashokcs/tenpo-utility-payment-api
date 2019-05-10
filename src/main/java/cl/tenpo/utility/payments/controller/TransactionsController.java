package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.entity.Transferencia;
import cl.tenpo.utility.payments.jpa.entity.Webpay;
import cl.tenpo.utility.payments.object.dto.ReceiptRequest;
import cl.tenpo.utility.payments.object.dto.TransactionCheckoutRequest;
import cl.tenpo.utility.payments.object.dto.TransactionPutRequest;
import cl.tenpo.utility.payments.object.dto.TransactionRequest;
import cl.tenpo.utility.payments.object.dto.TransferenciaOrderResponse;
import cl.tenpo.utility.payments.object.dto.WebpayInitResponse;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.service.TransferenciaService;
import cl.tenpo.utility.payments.service.WebpayService;
import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.util.Utils;
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
	private final BillService billService;
	private final TransactionService transactionService;
	private final TransferenciaClient transferenciaClient;
	private final TransferenciaService transferenciaService;
	private final WebpayClient webpayClient;
	private final WebpayService webpayService;

	public TransactionsController(
		final BillService billService,
		final TransactionService transactionService,
		final TransferenciaClient transferenciaClient,
		final TransferenciaService transferenciaService,
		final WebpayService webpayService,
		final WebpayClient webpayClient
	){
		this.billService = billService;
		this.transactionService = transactionService;
		this.transferenciaClient = transferenciaClient;
		this.transferenciaService = transferenciaService;
		this.webpayService = webpayService;
		this.webpayClient = webpayClient;
	}

	private final String ID = "{id:[0-9a-f\\-]{36}}";
	private final String BILL_ID = "{billId:[0-9a-f\\-]{36}}";

	private final String TRANSACTION_CREATE 	 = "/v1/transactions";
	private final String TRANSACTION_GET 		 = "/v1/transactions/"+ID;
	private final String TRANSACTION_PUT 		 = "/v1/transactions/"+ID;
	private final String TRANSACTION_BILL_DELETE = "/v1/transactions/"+ID+"/bills/"+BILL_ID;
	private final String TRANSACTION_CHECKOUT    = "/v1/transactions/"+ID+"/checkout";
	private final String TRANSACITON_RECEIPT 	 = "/v1/transactions/"+ID+"/receipt";

	@Transactional
	@PostMapping(path = TRANSACTION_CREATE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Transaction create(@RequestBody @Valid final TransactionRequest request)
	{
		// check for duplicates ids
		final Optional<String> duplicated = isBillIdsDuplicated(request.getBills());
		if (duplicated.isPresent()) {
			throw Http.ConficDuplicatedBillId(duplicated.get());
		}

		// get bills
		final List<Bill> bills = new ArrayList<>();
		for (final String requestBillId : request.getBills()) {
			bills.add(billService.findCreatedByPublicId(requestBillId).orElseThrow(() -> Http.BillNotFound(requestBillId)));
		}

		// check duplicates identifiers
		final List<Bill> reversed = new ArrayList<>(bills);
		Collections.reverse(reversed);
		for (final Bill bill : reversed) {
			if (isUtilityDuplicated(bills, bill)) throw Http.ConficDuplicatedIdentifier(bill.getPublicId());
		}

		// create transaction
		final Transaction transaction = new Transaction();
		transaction.setStatus(Transaction.PENDING);
		transaction.setPublicId(Utils.uuid());
		transaction.setAmount(bills.stream().mapToLong(b -> b.getAmount()).sum());
		transactionService.saveAndRefresh(transaction).orElseThrow(Http::ServerError);
		transaction.setBills(bills);

		// update bills
		for (final Bill bill : transaction.getBills()) {
			bill.setTransactionId(transaction.getId());
			bill.setStatus(Bill.PENDING);
			billService.save(bill).orElseThrow(Http::ServerError);
		}

		return transaction;
	}

	@GetMapping(TRANSACTION_GET)
	public Transaction get(@PathVariable("id") final String publicId)
	{
		// get transaction and bills
		final Transaction transaction = transactionService.findByPublicId(publicId).orElseThrow(Http::TransactionNotFound);
		transaction.setBills(billService.findByTransactionId(transaction.getId()));

		// get payment method
		if (transaction.getPaymentMethod() != null) {
			if (transaction.getPaymentMethod().equals(Transaction.WEBPAY)) {
				transaction.setWebpay(webpayService.findByTransactionId(transaction.getId()).orElse(null));
			} else if (transaction.getPaymentMethod().equals(Transaction.TRANSFERENCIA)) {
				transaction.setTransferencia(transferenciaService.findByTransactionId(transaction.getId()).orElse(null));
			}
		}

		return transaction;
	}

	@Transactional
	@PutMapping(path = TRANSACTION_PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Transaction put(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final TransactionPutRequest request
	){
		// get resources
		final Transaction transaction = transactionService.getCreatedOrPendingByPublicId(publicId).orElseThrow(Http::TransactionNotFound);
		final Bill bill = billService.findCreatedByPublicId(request.getBill()).orElseThrow(Http::BillNotFound);

		// get bills
		final List<Bill> bills = billService.findByTransactionId(transaction.getId());

		// check if already added
		if (isUtilityDuplicated(bills, bill)) throw Http.ConficDuplicatedIdentifier();

		// check list size
		if (bills.size() >= 15) throw Http.ConfictMaxSizeReached();

		// update bill
		bill.setTransactionId(transaction.getId());
		bill.setStatus(Bill.PENDING);
		billService.save(bill).orElseThrow(Http::ServerError);
		bills.add(bill);

		// update transaction
		transaction.setStatus(Transaction.PENDING);
		transaction.setAmount(bills.stream().mapToLong(b -> b.getAmount()).sum());
		transactionService.save(transaction).orElseThrow(Http::ServerError);
		transaction.setBills(bills);

		return transaction;
	}

	@Transactional
	@DeleteMapping(path = TRANSACTION_BILL_DELETE)
	public Transaction delete(
		@PathVariable("id") final String transactionPublicId,
		@PathVariable("billId") final String billPublicId
	){
		// get resources
		final Transaction transaction = transactionService.getPendingByPublicId(transactionPublicId).orElseThrow(Http::TransactionNotFound);
		final Bill bill = billService.findPendingByPublicIdAndTransactionId(billPublicId, transaction.getId()).orElseThrow(Http::BillNotFound);

		// remove bill from transaction
		bill.setTransactionId(null);
		bill.setStatus(Bill.CREATED);
		billService.save(bill).orElseThrow(Http::ServerError);

		// update transaction
		final List<Bill> bills = billService.findByTransactionId(transaction.getId());
		transaction.setBills(bills);
		transaction.setStatus(bills.size() > 0 ? Transaction.PENDING : Transaction.CREATED);
		transaction.setAmount(bills.stream().mapToLong(b -> b.getAmount()).sum());
		transactionService.save(transaction).orElseThrow(Http::ServerError);

		return transaction;
	}

	@Transactional
	@PostMapping(path = TRANSACTION_CHECKOUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Transaction checkout(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final TransactionCheckoutRequest request
	){
		// get request transaction
		final String paymentMethod = Utils.getPaymentMethod(request.getPaymentMethod()).orElseThrow(Http::InvalidPaymentMethod);
		final String email = request.getEmail();
		final Transaction transaction = transactionService.getPendingByPublicId(publicId).orElseThrow(Http::TransactionNotFound);

		// update transaction
		transaction.setStatus(Transaction.WAITING);
		transaction.setPaymentMethod(paymentMethod);
		transaction.setEmail(email);
		transactionService.save(transaction).orElseThrow(Http::ServerError);

		// get bills and update
		final List<Bill> bills = billService.findByTransactionId(transaction.getId());
		for (final Bill bill : bills) {
			if (bill.getStatus().equals(Bill.PENDING)) {
				bill.setStatus(Bill.WAITING);
				billService.save(bill).orElseThrow(Http::ServerError);
			} else {
				throw Http.ServerError();
			}
		}

		// init payment method
		if (Transaction.WEBPAY.equals(transaction.getPaymentMethod())) {
			final String webpayPublicId = Utils.uuid();
			final WebpayInitResponse webpayInitResponse = webpayClient.init(transaction, webpayPublicId).orElseThrow(Http::ServerError);
			final Webpay webpay = new Webpay();
			webpay.setStatus(Webpay.WAITING);
			webpay.setPublicId(webpayPublicId);
			webpay.setTransactionId(transaction.getId());
			webpay.setUrl(webpayInitResponse.getUrl());
			webpay.setToken(webpayInitResponse.getToken());
			webpayService.save(webpay).orElseThrow(Http::ServerError);
			transaction.setWebpay(webpay);
		}

		if (Transaction.TRANSFERENCIA.equals(transaction.getPaymentMethod())) {
			final String tefPublicId = Utils.uuid();
			final String tefNotifyId = Utils.uuid();
			final TransferenciaOrderResponse tefOrder = transferenciaClient.createOrder(transaction, tefPublicId, tefNotifyId).orElseThrow(Http::ServerError);
			final Transferencia transferencia = new Transferencia();
			transferencia.setStatus(Transferencia.WAITING);
			transferencia.setPublicId(tefPublicId);
			transferencia.setTransactionId(transaction.getId());
			transferencia.setNotifyId(tefNotifyId);
			transferencia.setUrl(tefOrder.getRedirectUrl());
			transferencia.setOrder(tefOrder.getMcOrderId());
			transferenciaService.save(transferencia).orElseThrow(Http::ServerError);
			transaction.setTransferencia(transferencia);
		}

		transaction.setBills(bills);
		return transaction;
	}

	@PostMapping(TRANSACITON_RECEIPT)
	public void receipt(
		@PathVariable("id") final String publicId,
		@RequestBody @Valid final ReceiptRequest receiptRequest
	){
		// TODO
	}

	/*
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
	}*/

	private boolean isUtilityDuplicated(final List<Bill> bills, final Bill bill)
	{
		return bills.stream().anyMatch(b -> b.getUtilityId().equals(bill.getUtilityId())
				&& b.getIdentifier().equals(bill.getIdentifier()) && !b.getPublicId().equals(bill.getPublicId()));
	}

	private Optional<String> isBillIdsDuplicated(final List<String> bills)
	{
		return bills.stream().filter(i -> Collections.frequency(bills, i) >1)
			.findAny();
	}
}
