package cl.tenpo.utility.payments.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>
{
	List<Category> findAllByStatusOrderByOrderAsc(final String status);
}
