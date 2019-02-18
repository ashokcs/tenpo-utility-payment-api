package cl.multipay.utility.payments.event.listener;

import java.util.Calendar;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import cl.multipay.utility.payments.entity.TotaliserDay;
import cl.multipay.utility.payments.entity.TotaliserMonth;
import cl.multipay.utility.payments.entity.TotaliserYear;
import cl.multipay.utility.payments.event.TotaliserEvent;
import cl.multipay.utility.payments.repository.TotaliserDayRepository;
import cl.multipay.utility.payments.repository.TotaliserMonthRepository;
import cl.multipay.utility.payments.repository.TotaliserYearRepository;

@Component
public class TotaliserListener
{
	private static final Logger logger = LoggerFactory.getLogger(TotaliserListener.class);

	private final TotaliserYearRepository totaliserYearRepository;
	private final TotaliserMonthRepository totaliserMonthRepository;
	private final TotaliserDayRepository totaliserDayRepository;

	public TotaliserListener(final TotaliserYearRepository totaliserYearRepository,
		final TotaliserMonthRepository totaliserMonthRepository,
		final TotaliserDayRepository totaliserDayRepository)
	{
		this.totaliserYearRepository = totaliserYearRepository;
		this.totaliserMonthRepository = totaliserMonthRepository;
		this.totaliserDayRepository = totaliserDayRepository;
	}

	@Async
	@EventListener
	@Transactional
	public void onMessage(final TotaliserEvent event)
	{
		try {
			logger.info("start");
			final Integer year = Calendar.getInstance().get(Calendar.YEAR);
			final Integer month = Calendar.getInstance().get(Calendar.MONTH) + 1;
			final Integer day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

			// yearly
			final Optional<TotaliserYear> totaliserYearOptional = totaliserYearRepository.findByYear(year);
			if (totaliserYearOptional.isPresent()) {
				final TotaliserYear totaliserYear = totaliserYearOptional.get();
				totaliserYear.setQuantity(totaliserYear.getQuantity() + 1);
				totaliserYear.setAmount(totaliserYear.getAmount() + event.getAmount());
				totaliserYearRepository.save(totaliserYear);
			} else {
				final TotaliserYear totaliserYear = new TotaliserYear();
				totaliserYear.setYear(year);
				totaliserYear.setQuantity(1l);
				totaliserYear.setAmount(event.getAmount());
				totaliserYearRepository.save(totaliserYear);
			}

			// monthly
			final Optional<TotaliserMonth> totaliserMonthOptional = totaliserMonthRepository.findByYearAndMonth(year, month);
			if (totaliserMonthOptional.isPresent()) {
				final TotaliserMonth totaliserMonth = totaliserMonthOptional.get();
				totaliserMonth.setQuantity(totaliserMonth.getQuantity() + 1);
				totaliserMonth.setAmount(totaliserMonth.getAmount() + event.getAmount());
				totaliserMonthRepository.save(totaliserMonth);
			} else {
				final TotaliserMonth totaliserMonth = new TotaliserMonth();
				totaliserMonth.setYear(year);
				totaliserMonth.setMonth(month);
				totaliserMonth.setQuantity(1l);
				totaliserMonth.setAmount(event.getAmount());
				totaliserMonthRepository.save(totaliserMonth);
			}

			// daily
			final Optional<TotaliserDay> totaliserDayOptional = totaliserDayRepository.findByYearAndMonthAndDay(year, month, day);
			if (totaliserDayOptional.isPresent()) {
				final TotaliserDay totaliserDay = totaliserDayOptional.get();
				totaliserDay.setQuantity(totaliserDay.getQuantity() + 1);
				totaliserDay.setAmount(totaliserDay.getAmount() + event.getAmount());
				totaliserDayRepository.save(totaliserDay);
			} else {
				final TotaliserDay totaliserDay = new TotaliserDay();
				totaliserDay.setYear(year);
				totaliserDay.setMonth(month);
				totaliserDay.setDay(day);
				totaliserDay.setQuantity(1l);
				totaliserDay.setAmount(event.getAmount());
				totaliserDayRepository.save(totaliserDay);
			}
			logger.info("finnish");
		} catch (final Exception e) {
			 logger.error(e.getMessage());
		}
	}
}
