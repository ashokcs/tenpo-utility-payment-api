package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentEft;

public interface UtilityPaymentEftRepository extends JpaRepository<UtilityPaymentEft, Long>
{
	public Optional<UtilityPaymentEft> findByTransactionId(final Long transactionId);
	public Optional<UtilityPaymentEft> findByPublicId(final String publicId);
	public Optional<UtilityPaymentEft> findByPublicIdAndStatus(final String publicId, final String status);
	public Optional<UtilityPaymentEft> findByPublicIdAndNotifyIdAndStatus(final String publicId, final String notifyId, final String status);
}
