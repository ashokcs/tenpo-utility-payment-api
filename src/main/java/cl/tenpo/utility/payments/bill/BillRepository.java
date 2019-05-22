package cl.tenpo.utility.payments.bill;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long>
{
	public Optional<Bill> findByPublicIdAndStatus(final String publicId, final String status);
	public Optional<Bill> findByPublicIdAndTransactionIdAndStatus(final String publicId, final Long transactionId, final String status);
	public List<Bill> findByTransactionId(final Long transactionId);
	public List<Bill> findByTransactionIdAndStatus(final Long transactionId, final String status);
}
