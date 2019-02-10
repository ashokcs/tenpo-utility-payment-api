package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.WebpayPayment;

public interface WebpayPaymentRepository extends JpaRepository<WebpayPayment, Long>
{
	public Optional<WebpayPayment> findByTokenAndStatus(final String token, final String status);
}
