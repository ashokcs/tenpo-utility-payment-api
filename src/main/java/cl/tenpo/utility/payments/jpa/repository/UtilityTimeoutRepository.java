package cl.tenpo.utility.payments.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.UtilityTimeout;

public interface UtilityTimeoutRepository extends JpaRepository<UtilityTimeout, Long>
{
	public Optional<UtilityTimeout> findByUtilityId(final Long utilityId);
}
