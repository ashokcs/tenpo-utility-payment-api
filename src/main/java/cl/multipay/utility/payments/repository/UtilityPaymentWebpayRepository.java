package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentWebpay;

public interface UtilityPaymentWebpayRepository extends JpaRepository<UtilityPaymentWebpay, Long>
{
	public Optional<UtilityPaymentWebpay> findByTokenAndStatus(final String token, final String status);
	public Optional<UtilityPaymentWebpay> findByTransactionId(final Long transactionId);
}
