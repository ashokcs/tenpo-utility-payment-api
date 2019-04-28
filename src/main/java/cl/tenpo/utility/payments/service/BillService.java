package cl.tenpo.utility.payments.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.tenpo.utility.payments.exception.NotFoundException;
import cl.tenpo.utility.payments.jpa.entity.Bill;
import cl.tenpo.utility.payments.jpa.entity.Utility;
import cl.tenpo.utility.payments.jpa.repository.BillRepository;

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

	public Optional<Bill> findWaitingByPublicId(final String publicId)
	{
		try {
			final Optional<Bill> opt = billRepository.findByPublicIdAndStatus(publicId, Bill.WAITING);
			if (opt.isPresent()) {
				final Bill bill = opt.get();
				final Utility utility = utilityService.findById(bill.getUtilityId()).orElseThrow(NotFoundException::new);
				bill.setUtility(utility);
				return Optional.of(bill);
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Bill> findByTransactionId(final Long transactionId)
	{
		try {
			final List<Bill> res = billRepository.findByTransactionId(transactionId);
			if (res.size() == 1) {
				final Bill bill = res.get(0);
				final Utility utility = utilityService.findById(bill.getUtilityId()).orElse(null);
				if (utility != null) {
					bill.setUtility(utility);
					return Optional.of(res.get(0));
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Bill> getWaitingByTransactionId(final Long transactionId)
	{
		try {
			final List<Bill> res = billRepository.findByTransactionIdAndStatus(transactionId, Bill.WAITING);
			if (res.size() == 1) {
				final Bill bill = res.get(0);
				final Utility utility = utilityService.findById(bill.getUtilityId()).orElse(null);
				if (utility != null) {
					bill.setUtility(utility);
					return Optional.of(res.get(0));
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
