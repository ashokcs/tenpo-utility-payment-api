package cl.multipay.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.Bill;

public interface BillRepository extends JpaRepository<Bill, Long>
{
	public Bill findByPublicId(final String publicId);
}