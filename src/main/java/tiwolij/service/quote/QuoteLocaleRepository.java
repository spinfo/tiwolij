package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.RecordId;
import tiwolij.domain.QuoteLocale;

public interface QuoteLocaleRepository extends CrudRepository<QuoteLocale, Integer> {

	public QuoteLocale findTop1ById(Integer localeId);

	public QuoteLocale findTop1BySchedule(String schedule);

	public QuoteLocale findTop1ByScheduleAndLanguage(String schedule, String language);

	public QuoteLocale findTop1ByQuoteIdAndLanguage(Integer quoteId, String language);

	public List<QuoteLocale> findAll();

	public List<QuoteLocale> findAllByQuoteId(Integer quoteId);

	public List<QuoteLocale> findAllBySchedule(String schedule);

	public List<RecordId> findAllByLanguage(String language);

	public List<QuoteLocale> findAllByScheduleAndLanguage(String schedule, String language);

}
