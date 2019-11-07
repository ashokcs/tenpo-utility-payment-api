package cl.tenpo.utility.payments.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>
{
	List<Category> findAllByStatusOrderByNameAsc(final String status);
}
