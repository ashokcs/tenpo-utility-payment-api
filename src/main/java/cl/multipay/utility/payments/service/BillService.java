package cl.multipay.utility.payments.service;

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

	public boolean save(final Bill bill)
	{
		try {
			billRepository.save(bill);
			return true;
		} catch (final Throwable e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	public Bill findByPublicId(final String publicId)
	{
		return billRepository.findByPublicId(publicId);
	}
}
