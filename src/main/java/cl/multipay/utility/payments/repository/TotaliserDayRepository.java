package cl.multipay.utility.payments.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.multipay.utility.payments.entity.TotaliserDay;

public interface TotaliserDayRepository extends JpaRepository<TotaliserDay, Long>
{
	Optional<TotaliserDay> findByYearAndMonthAndDay(final Integer year, final Integer month, final Integer day);
}
