package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Webpay;

public interface WebpayRepository extends JpaRepository<Webpay, Long>
{
	public Optional<Webpay> findByTransactionId(final Long transactionId);
	public Optional<Webpay> findByPublicIdAndTokenAndStatus(final String publicId, final String token, final String status);
}
