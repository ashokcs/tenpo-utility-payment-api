package cl.multipay.utility.payments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cl.multipay.utility.payments.entity.UtilityPaymentMethod;
import cl.multipay.utility.payments.repository.UtilityPaymentMethodRepository;

@Service
public class UtilityPaymentMethodService
{
	private static final Logger logger = LoggerFactory.getLogger(UtilityPaymentMethodService.class);

	private final UtilityPaymentMethodRepository utilityPaymentMethodRepository;

	public UtilityPaymentMethodService(final UtilityPaymentMethodRepository utilityPaymentMethodRepository)
	{
		this.utilityPaymentMethodRepository =  utilityPaymentMethodRepository;
	}

	public boolean save(final UtilityPaymentMethod utilityPaymentMethod)
	{
		try {
			utilityPaymentMethodRepository.save(utilityPaymentMethod);
			return true;
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
}
