package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentMethod;

public interface UtilityPaymentMethodRepository extends JpaRepository<UtilityPaymentMethod, Long>
{

}
