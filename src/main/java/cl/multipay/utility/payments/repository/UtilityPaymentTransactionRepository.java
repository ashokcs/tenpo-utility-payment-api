package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentTransaction;

public interface UtilityPaymentTransactionRepository extends JpaRepository<UtilityPaymentTransaction, Long>
{
	public Optional<UtilityPaymentTransaction> findByPublicId(final String publicId);
	public Optional<UtilityPaymentTransaction> findByPublicIdAndStatus(final String publicId, final String status);
	public Optional<UtilityPaymentTransaction> findByIdAndStatus(final Long id, final String status);
}
