package cl.tenpo.utility.payments.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Utility;

public interface UtilityRepository extends JpaRepository<Utility, Long>
{
	List<Utility> findAllByOrderByIdAsc();
	List<Utility> findAllByCategoryIdOrderByIdAsc(final Long categoryId);
	long countByCategoryId(final Long categoryId);
}
