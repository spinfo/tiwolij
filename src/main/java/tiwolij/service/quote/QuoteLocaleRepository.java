package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.QuoteLocale;

public interface QuoteLocaleRepository extends CrudRepository<QuoteLocale, Integer> {

	public QuoteLocale findOneById(Integer localeId);

	public QuoteLocale findOneBySchedule(String schedule);

	public QuoteLocale findOneByScheduleAndLanguage(String schedule, String language);

	public QuoteLocale findOneByQuoteIdAndLanguage(Integer quoteId, String language);

	public List<QuoteLocale> findAll();

	public List<QuoteLocale> findAllByQuoteId(Integer quoteId);

	public List<QuoteLocale> findAllBySchedule(String schedule);

	public List<QuoteLocale> findAllByScheduleAndLanguage(String schedule, String language);

}
