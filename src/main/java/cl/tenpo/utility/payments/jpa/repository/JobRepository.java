package cl.tenpo.utility.payments.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.tenpo.utility.payments.jpa.entity.Job;

public interface JobRepository extends JpaRepository<Job, Long>
{

}
