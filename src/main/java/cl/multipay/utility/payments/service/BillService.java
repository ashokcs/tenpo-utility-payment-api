package cl.multipay.utility.payments.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.repository.BillRepository;

@Service
public class BillService
{
	private static final Logger logger = LoggerFactory.getLogger(BillService.class);

	private final BillRepository billRepository;

	public BillService(final BillRepository billRepository)
	{
		this.billRepository = billRepository;
	}

	public Optional<Bill> save(final Bill bill)
	{
		try {
			return Optional.of(billRepository.save(bill));
		} catch (final Throwable e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Bill> findById(final Long id)
	{
		return billRepository.findById(id);
	}

	public Optional<Bill> findByPublicId(final String publicId)
	{
		return billRepository.findByPublicId(publicId);
	}

	public Optional<Bill> findByPublicId(final String publicId, final String status)
	{
		return billRepository.findByPublicIdAndStatus(publicId, status);
	}

	public Optional<Bill> getWaitingById(final Long id)
	{
		return billRepository.findByIdAndStatus(id, Bill.WAITING);
	}

	public Optional<Bill> getSucceedById(final Long id)
	{
		return billRepository.findByIdAndStatus(id, Bill.SUCCEED);
	}
}
