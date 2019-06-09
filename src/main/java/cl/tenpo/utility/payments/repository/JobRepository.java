package cl.tenpo.utility.payments.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long>
{

}
