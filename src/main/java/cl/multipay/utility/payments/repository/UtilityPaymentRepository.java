package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPayment;

public interface UtilityPaymentRepository extends JpaRepository<UtilityPayment, Long>
{

}