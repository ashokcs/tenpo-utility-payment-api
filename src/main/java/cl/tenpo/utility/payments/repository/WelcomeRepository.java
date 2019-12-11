package cl.tenpo.utility.payments.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Welcome;

public interface WelcomeRepository extends JpaRepository<Welcome, Long>
{
	public Optional<Welcome> findByUser(final UUID user);
}