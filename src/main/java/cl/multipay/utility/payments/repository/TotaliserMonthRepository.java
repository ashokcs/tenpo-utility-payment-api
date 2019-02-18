package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.TotaliserMonth;

public interface TotaliserMonthRepository extends JpaRepository<TotaliserMonth, Long>
{
	Optional<TotaliserMonth> findByYearAndMonth(final Integer year, final Integer month);
}
