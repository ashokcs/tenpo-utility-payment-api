package cl.tenpo.utility.payments.job;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JobService
{
	private static final Logger logger = LoggerFactory.getLogger(JobService.class);

	private final JobRepository jobRepository;

	public JobService(final JobRepository jobRepository)
	{
		this.jobRepository =  jobRepository;
	}

	public Optional<Job> save(final Job job)
	{
		try {
			return Optional.of(jobRepository.save(job));
		} catch (final Exception e) {
			logger.error(e.getMessage(), e);
		}
		return Optional.empty();
	}
}
