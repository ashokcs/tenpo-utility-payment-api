package cl.tenpo.utility.payments.utility;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilityRepository extends JpaRepository<Utility, Long>
{
	List<Utility> findAllByOrderByIdAsc();
	List<Utility> findAllByCategoryIdOrderByIdAsc(final Long categoryId);
	long countByCategoryId(final Long categoryId);
}
