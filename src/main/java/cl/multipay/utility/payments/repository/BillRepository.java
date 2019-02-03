package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long>
{
	public Optional<Bill> findByPublicId(final String publicId);
	public Optional<Bill> findByPublicIdAndStatus(final String publicId, final Long status);
}