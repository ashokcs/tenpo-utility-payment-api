package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>
{

}
