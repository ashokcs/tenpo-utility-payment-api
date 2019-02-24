package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.UtilityPaymentBill;

public interface UtilityPaymentBillRepository extends JpaRepository<UtilityPaymentBill, Long>
{
	public Optional<UtilityPaymentBill> findByTransactionId(final Long transactionId);
}
