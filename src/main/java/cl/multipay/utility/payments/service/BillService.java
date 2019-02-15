package cl.multipay.utility.payments.service;

import java.util.Optional;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.Bill;
import cl.multipay.utility.payments.repository.BillRepository;

@Service
public class BillService
{
	private static final Logger logger = LoggerFactory.getLogger(BillService.class);

	private final EntityManager entityManager;
	private final BillRepository billRepository;

	public BillService(final BillRepository billRepository, final EntityManager entityManager)
	{
		this.billRepository = billRepository;
		this.entityManager = entityManager;
	}

	@Transactional
	public Optional<Bill> save(final Bill bill)
	{
		try {
			final Bill saved = billRepository.save(bill);
			entityManager.refresh(saved);
			return Optional.of(saved);
		} catch (final Exception		 e) {
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

	public Optional<Bill> getPendingByPublicId(final String publicId)
	{
		return billRepository.findByPublicIdAndStatus(publicId, Bill.PENDING);
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
