package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentEft;

public interface UtilityPaymentEftRepository extends JpaRepository<UtilityPaymentEft, Long>
{
	Optional<UtilityPaymentEft> findByTransactionId(final Long transactionId);
	Optional<UtilityPaymentEft> findByPublicId(final String publicId);
	Optional<UtilityPaymentEft> findByPublicIdAndStatus(final String publicId, final String status);
	Optional<UtilityPaymentEft> findByPublicIdAndNotifyIdAndStatus(final String publicId, final String notifyId, final String status);
}
