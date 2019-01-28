package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentIntent;

public interface UtilityPaymentIntentRepository extends JpaRepository<UtilityPaymentIntent, Long>
{
	public UtilityPaymentIntent findByUuid(final String uuid);
}