package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID>
{
	Optional<Payment> findByBillId(final UUID billId);
}
