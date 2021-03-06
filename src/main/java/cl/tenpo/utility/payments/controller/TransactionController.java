package cl.tenpo.utility.payments.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.newrelic.api.agent.NewRelic;

import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Favorite;
import cl.tenpo.utility.payments.jpa.entity.Job;
import cl.tenpo.utility.payments.jpa.entity.Payment;
import cl.tenpo.utility.payments.jpa.entity.PaymentMethod;
import cl.tenpo.utility.payments.jpa.entity.Transaction;
import cl.tenpo.utility.payments.jpa.repository.FavoriteRepository;
import cl.tenpo.utility.payments.object.Balance;
import cl.tenpo.utility.payments.object.TransactionRequest;
import cl.tenpo.utility.payments.object.UserAccount;
import cl.tenpo.utility.payments.service.BillService;
import cl.tenpo.utility.payments.service.JobService;
import cl.tenpo.utility.payments.service.NatsService;
import cl.tenpo.utility.payments.service.PaymentService;
import cl.tenpo.utility.payments.service.TransactionService;
import cl.tenpo.utility.payments.util.AccountMsClient;
import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.util.PrepaidClient;
import cl.tenpo.utility.payments.util.Properties;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class TransactionController
{
	private final BillService billService;
	private final FavoriteRepository favoriteRepository;
	private final JobService jobService;
	private final NatsService natsService;
	private final PaymentService paymentService;
	private final PrepaidClient prepaidClient;
	private final Properties properties;
	private final AccountMsClient accountMsClient;
	private final TransactionService transactionService;

	public TransactionController(
		final BillService billService,
		final FavoriteRepository favoriteRepository,
		final JobService jobService,
		final NatsService natsService,
		final PaymentService paymentService,
		final PrepaidClient prepaidClient,
		final Properties properties,
		final AccountMsClient accountMsClient,
		final TransactionService transactionService
	){
		this.billService = billService;
		this.favoriteRepository = favoriteRepository;
		this.jobService = jobService;
		this.natsService = natsService;
		this.paymentService = paymentService;
		this.prepaidClient = prepaidClient;
		this.properties = properties;
		this.accountMsClient = accountMsClient;
		this.transactionService = transactionService;
	}

	@Transactional
	@PostMapping(path = "/v1/utility-payments/transactions", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Transaction create(
		@RequestBody @Valid final TransactionRequest request,
		@RequestHeader(value="x-mine-user-id") final UUID userId
	){
		// check nats status
		if (natsService.isNatsDisconnected()) {
			NewRelic.noticeError("Nats Unavailable");
			throw Http.ServiceUnavailable();
		}

		// check duplicates ids
		final Optional<UUID> duplicated = getDuplicatedBillId(request.getBills());
		if (duplicated.isPresent()) {
			throw Http.ConficDuplicatedBillId(duplicated.get());
		}

		// get bills
		final List<Bill> bills = new ArrayList<>();
		request.getBills().forEach(uuid -> {
			bills.add(billService.findCreatedById(uuid, userId).orElseThrow(() -> Http.BillNotFound(uuid)));
		});

		// check duplicates identifiers
		final List<Bill> reversed = new ArrayList<>(bills);
		Collections.reverse(reversed);
		reversed.forEach(r -> {
			if (isUtilityIdentifierDuplicated(bills, r))
				throw Http.ConficDuplicatedIdentifier(r.getId());
		});

		// get account id from account-ms
		final Optional<UserAccount> aopt = accountMsClient.getAccount(userId);
		if (!aopt.isPresent()) {
			throw Http.NotFound();
		}

		// check user balance
		final Balance balance = prepaidClient.balance(userId, aopt.get().getAccountNumber()).getBalance().orElseThrow(Http::ServerError);
		final Long totalAmount = bills.stream().mapToLong(b -> b.getAmount()).sum();
		if (balance.isUpdated() && balance.getBalance().getValue().compareTo(totalAmount) < 0) {
			throw Http.ConfictNotEnoughBalance();
		}

		// create transaction
		final Transaction transaction = new Transaction();
		transaction.setStatus(Transaction.PROCESSING);
		transaction.setOrder(transactionService.getNextval().orElseThrow(Http::ServerError));
		transaction.setUser(userId);
		transaction.setAmount(totalAmount);
		transaction.setAmountSucceeded(0L);
		transactionService.save(transaction).orElseThrow(Http::ServerError);
		transaction.setBills(bills);

		// update bills
		transaction.getBills().forEach(b -> {
			// set bills processing
			b.setTransactionId(transaction.getId());
			b.setStatus(Bill.PROCESSING);
			billService.save(b).orElseThrow(Http::ServerError);

			// save pending payment
			final Payment payment = new Payment();
			payment.setStatus(Payment.PROCESSING);
			payment.setTransactionId(transaction.getId());
			payment.setBillId(b.getId());
			payment.setPaymentMethodId(PaymentMethod.PREPAID);
			payment.setAmount(b.getAmount());
			paymentService.save(payment).orElseThrow(Http::ServerError);;
		});

		// save job
		jobService.save(new Job(transaction.getId(), properties.jobMaxAttempts));

		// fire event
		natsService.publish(properties.natsTransactionCreated, transaction.getId().toString().getBytes());

		// find favorite names
		transaction.getBills().forEach(b -> {
			final Optional<Favorite> fav = favoriteRepository.findByUserAndUtilityIdAndIdentifier(userId, b.getUtilityId(), b.getIdentifier());
			if (fav.isPresent()) {
				b.setName(fav.get().getName());
			}
		});

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
