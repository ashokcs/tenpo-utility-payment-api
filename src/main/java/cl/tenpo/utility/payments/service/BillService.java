package cl.tenpo.utility.payments.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.entity.Bill;
import cl.tenpo.utility.payments.entity.Utility;
import cl.tenpo.utility.payments.repository.BillRepository;
import cl.tenpo.utility.payments.util.Http;

@Service
public class BillService
{
	private static final Logger logger = LoggerFactory.getLogger(BillService.class);

	private final BillRepository billRepository;
	private final UtilityService utilityService;

	public BillService(
		final BillRepository billRepository,
		final UtilityService utilityService
	){
		this.billRepository = billRepository;
		this.utilityService = utilityService;
	}

	public Optional<Bill> save(final Bill bill)
	{
		try {
			return Optional.of(billRepository.save(bill));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Bill> findCreatedById(final UUID id, final UUID user)
	{
		return findByIdAndStatus(id, user, Bill.CREATED);
	}

	public Optional<Bill> findByIdAndStatus(final UUID id, final UUID user, final String status)
	{
		try {
			final Optional<Bill> opt = billRepository.findByIdAndUserAndStatus(id, user, status);
			if (opt.isPresent()) {
				final Bill bill = opt.get();
				final Utility utility = utilityService.findUtilityById(bill.getUtilityId()).orElseThrow(Http::NotFound);
				bill.setUtility(utility);
				return Optional.of(bill);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public List<Bill> findByTransactionId(final UUID user, final UUID transactionId)
	{
		try {
			final List<Bill> res = billRepository.findByUserAndTransactionId(user, transactionId);
			for (final Bill bill : res) {
				final Utility utility = utilityService.findUtilityById(bill.getUtilityId()).orElse(null);
				if (utility != null) {
					bill.setUtility(utility);
				}
			}
			return res;
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}
}
