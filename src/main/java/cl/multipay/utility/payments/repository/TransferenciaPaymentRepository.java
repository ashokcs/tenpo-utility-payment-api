package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.TransferenciaPayment;

public interface TransferenciaPaymentRepository extends JpaRepository<TransferenciaPayment, Long>
{

}
