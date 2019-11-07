package cl.tenpo.utility.payments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Utility;

public interface UtilityRepository extends JpaRepository<Utility, Long>
{
	List<Utility> findAllByOrderByIdAsc();
	List<Utility> findAllByCategoryIdOrderByIdAsc(final Long categoryId);
	long countByCategoryId(final Long categoryId);
}
