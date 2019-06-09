package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.tenpo.utility.payments.entity.Bill;
import cl.tenpo.utility.payments.entity.Job;
import cl.tenpo.utility.payments.entity.Transaction;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.JobService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.transaction.TransactionRequest;
import cl.tenpo.utility.payments.util.Http;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController
{
	private final BillService billService;
	private final JobService jobService;
	private final TransactionService transactionService;

	public TransactionController(
		final BillService billService,
		final JobService jobService,
		final TransactionService transactionService
	){
		this.billService = billService;
		this.jobService = jobService;
		this.transactionService = transactionService;
	}

	@GetMapping("/v1/utility-payments/transactions/{id:[0-9a-f\\-]{36}}")
	public Transaction get(@PathVariable("id") final UUID id)
	{
		// get transaction and bills
		final Transaction transaction = transactionService.findById(id).orElseThrow(Http::TransactionNotFound);
		transaction.setBills(billService.findByTransactionId(transaction.getId()));

		// get payment method
		// TODO
//		if (transaction.getPaymentMethod() != null) {
//			if (transaction.getPaymentMethod().equals(Transaction.WEBPAY)) {
//				transaction.setWebpay(webpayService.findByTransactionId(transaction.getId()).orElse(null));
//			} else if (transaction.getPaymentMethod().equals(Transaction.TRANSFERENCIA)) {
//				transaction.setTransferencia(transferenciaService.findByTransactionId(transaction.getId()).orElse(null));
//			}
//		}

		return transaction;
	}

	@Transactional
	@PostMapping(path = "/v1/utility-payments/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Transaction create(
		@RequestBody @Valid final TransactionRequest request,
		@RequestHeader(value="User-Id") final UUID userId
	){
		// TODO check user balance
		// ...

		// check duplicates ids
		final Optional<UUID> duplicated = getDuplicatedBillId(request.getBills());
		if (duplicated.isPresent()) {
			throw Http.ConficDuplicatedBillId(duplicated.get());
		}

		// get bills
		final List<Bill> bills = new ArrayList<>();
		request.getBills().forEach(uuid -> {
			bills.add(billService.findCreatedById(uuid).orElseThrow(() -> Http.BillNotFound(uuid)));
		});

		// check duplicates identifiers
		final List<Bill> reversed = new ArrayList<>(bills);
		Collections.reverse(reversed);
		reversed.forEach(r -> {
			if (isUtilityIdentifierDuplicated(bills, r))
				throw Http.ConficDuplicatedIdentifier(r.getId());
		});

		// create transaction
		final Transaction transaction = new Transaction();
		transaction.setStatus(Transaction.PROCESSING);
		transaction.setOrder(transactionService.generateOrderSequence().orElseThrow(Http::ServerError));
		transaction.setUser(userId);
		transaction.setAmount(bills.stream().mapToLong(b -> b.getAmount()).sum());
		transaction.setAmountSucceeded(0L);
		transaction.setAmountExpired(0L);
		transactionService.save(transaction).orElseThrow(Http::ServerError);
		transaction.setBills(bills);

		// update bills
		transaction.getBills().forEach(b -> {
			b.setTransactionId(transaction.getId());
			b.setStatus(Bill.PROCESSING);
			billService.save(b).orElseThrow(Http::ServerError);
		});

		// save job
		jobService.save(new Job(transaction.getId()));

		// return transaction
		return transaction;
	}

	private boolean isUtilityIdentifierDuplicated(final List<Bill> bills, final Bill bill)
	{
		return bills.stream()
				.anyMatch(b -> b.getUtilityId().equals(bill.getUtilityId())
				&& b.getIdentifier().equals(bill.getIdentifier()) && !b.getId().equals(bill.getId()));
	}

	private Optional<UUID> getDuplicatedBillId(final List<UUID> bills)
	{
		return bills.stream()
				.filter(i -> Collections.frequency(bills, i) > 1)
				.findAny();
	}
}
