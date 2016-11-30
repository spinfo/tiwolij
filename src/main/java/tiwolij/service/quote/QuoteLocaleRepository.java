package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.QuoteLocale;
import tiwolij.domain.RecordId;

public interface QuoteLocaleRepository extends PagingAndSortingRepository<QuoteLocale, Integer> {

	public QuoteLocale findTop1ById(Integer localeId);

	public QuoteLocale findTop1BySchedule(String schedule);

	public QuoteLocale findTop1ByScheduleAndLanguage(String schedule, String language);

	public QuoteLocale findTop1ByQuoteIdAndLanguage(Integer quoteId, String language);

	public List<QuoteLocale> findAll();

	public List<QuoteLocale> findAllByQuoteId(Integer quoteId);

	public List<QuoteLocale> findAllBySchedule(String schedule);

	public List<RecordId> findAllByLanguage(String language);

	public List<RecordId> findAllByScheduleAndLanguage(String schedule, String language);

	// pagination

	public Page<QuoteLocale> findAll(Pageable pageable);

	public Page<QuoteLocale> findAllByQuoteId(Pageable pageable, Integer quoteId);

	public Page<QuoteLocale> findAllBySchedule(Pageable pageable, String schedule);

	public Page<QuoteLocale> findAllByLanguage(Pageable pageable, String language);

	public Page<QuoteLocale> findAllByScheduleAndLanguage(Pageable pageable, String schedule, String language);

}
