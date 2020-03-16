package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Option;

public interface OptionRepository extends JpaRepository<Option, Long>
{
	Optional<Option> findByUser(final UUID user);
}
