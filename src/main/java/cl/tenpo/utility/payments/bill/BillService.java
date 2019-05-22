package cl.tenpo.utility.payments.bill;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.util.Http;
import cl.tenpo.utility.payments.utility.Utility;
import cl.tenpo.utility.payments.utility.UtilityService;

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

	public Optional<Bill> findCreatedByPublicId(final String publicId)
	{
		return findByPublicIdAndStatus(publicId, Bill.CREATED);
	}

	public Optional<Bill> findPendingByPublicId(final String publicId)
	{
		return findByPublicIdAndStatus(publicId, Bill.PENDING);
	}

	public Optional<Bill> findPendingByPublicIdAndTransactionId(final String publicId, final Long transactionId)
	{
		try {
			final Optional<Bill> opt = billRepository.findByPublicIdAndTransactionIdAndStatus(publicId, transactionId, Bill.PENDING);
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

	public Optional<Bill> findWaitingByPublicId(final String publicId)
	{
		return findByPublicIdAndStatus(publicId, Bill.WAITING);
	}

	public Optional<Bill> findByPublicIdAndStatus(final String publicId, final String status)
	{
		try {
			final Optional<Bill> opt = billRepository.findByPublicIdAndStatus(publicId, status);
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

	public List<Bill> findByTransactionId(final Long transactionId)
	{
		try {
			final List<Bill> res = billRepository.findByTransactionId(transactionId);
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

	public List<Bill> getWaitingByTransactionId(final Long transactionId)
	{
		try {
			final List<Bill> res = billRepository.findByTransactionIdAndStatus(transactionId, Bill.WAITING);
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
