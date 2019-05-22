package cl.tenpo.utility.payments.webpay;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WebpayRepository extends JpaRepository<Webpay, Long>
{
	public Optional<Webpay> findByTransactionId(final Long transactionId);
	public Optional<Webpay> findByPublicIdAndTokenAndStatus(final String publicId, final String token, final String status);
}
