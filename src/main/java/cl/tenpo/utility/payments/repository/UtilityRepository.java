package cl.tenpo.utility.payments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Utility;

public interface UtilityRepository extends JpaRepository<Utility, Long>
{
	List<Utility> findAllByCategoryIdAndStatusOrderByIdAsc(final Long categoryId, final String status);
	long countByCategoryId(final Long categoryId);
}
