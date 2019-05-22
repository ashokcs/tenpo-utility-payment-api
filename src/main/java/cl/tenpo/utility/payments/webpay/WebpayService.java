package cl.tenpo.utility.payments.webpay;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WebpayService
{
	private static final Logger logger = LoggerFactory.getLogger(WebpayService.class);

	private final WebpayRepository wr;

	public WebpayService(final WebpayRepository wr)
	{
		this.wr =  wr;
	}

	public Optional<Webpay> save(final Webpay payment)
	{
		try {
			return Optional.of(wr.save(payment));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Webpay> findByTransactionId(final Long transactionId)
	{
		try {
			return wr.findByTransactionId(transactionId);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Webpay> getWaitingByPublicIdAndToken(final String publicId, final String token)
	{
		try {
			return wr.findByPublicIdAndTokenAndStatus(publicId, token, Webpay.WAITING);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}

	public Optional<Webpay> getAckByPublicIdAndToken(final String publicId, final String token)
	{
		try {
			return wr.findByPublicIdAndTokenAndStatus(publicId, token, Webpay.ACKNOWLEDGED);
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
