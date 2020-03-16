package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Welcome;

public interface WelcomeRepository extends JpaRepository<Welcome, Long>
{
	public Optional<Welcome> findByUser(final UUID user);
}