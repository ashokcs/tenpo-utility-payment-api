package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.TotaliserYear;

public interface TotaliserYearRepository extends JpaRepository<TotaliserYear, Long>
{
	Optional<TotaliserYear> findByYear(final Integer year);
}
