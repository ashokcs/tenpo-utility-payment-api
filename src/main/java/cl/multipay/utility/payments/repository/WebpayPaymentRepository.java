package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.WebpayPayment;

public interface WebpayPaymentRepository extends JpaRepository<WebpayPayment, Long>
{

}
