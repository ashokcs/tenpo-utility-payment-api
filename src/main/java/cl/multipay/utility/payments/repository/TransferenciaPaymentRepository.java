package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.TransferenciaPayment;

public interface TransferenciaPaymentRepository extends JpaRepository<TransferenciaPayment, Long>
{
	Optional<TransferenciaPayment> findByPublicId(final String publicId);
	Optional<TransferenciaPayment> findByPublicIdAndStatus(final String publicId, final String status);
	Optional<TransferenciaPayment> findByPublicIdAndNotifyIdAndStatus(final String publicId, final String notifyId, final String status);
}
