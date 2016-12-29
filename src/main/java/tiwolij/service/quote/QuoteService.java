package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;

public interface QuoteService {

	public void delLocale(Integer localeId);

	public void delQuote(Integer quoteId);

	public Long getCount();

	public QuoteLocale getLocale(Integer localeId);

	public QuoteLocale getLocaleByQuoteAndLang(Integer quoteId, String language);

	public Long getLocaleCount();

	public QuoteLocale getLocaleRandomByLang(String language);

	public QuoteLocale getLocaleRandomByScheduleAndLang(String schedule, String language);

	public QuoteLocale getLocaleRandomNextByScheduleAndLang(String schedule, String language, Boolean prev)
			throws Exception;

	public List<QuoteLocale> getLocales();

	public Page<QuoteLocale> getLocales(Pageable pageable);

	public List<QuoteLocale> getLocalesByQuote(Integer quoteId);

	public Page<QuoteLocale> getLocalesByQuote(Pageable pageable, Integer quoteId);

	public Page<QuoteLocale> getLocalesBySchedule(Pageable pageable, String schedule);

	// pagination

	public List<QuoteLocale> getLocalesBySchedule(String schedule);

	public Page<QuoteLocale> getLocalesByScheduleAndLang(Pageable pageable, String schedule, String language);

	public List<QuoteLocale> getLocalesByScheduleAndLang(String schedule, String language);

	public Quote getQuote(Integer quoteId);

	public List<Quote> getQuotes();

	public Page<Quote> getQuotes(Pageable pageable);

	public List<Quote> getQuotesByWork(Integer workId);

	public Page<Quote> getQuotesByWork(Pageable pageable, Integer workId);

	public Boolean hasLocale(Integer quoteId, String language);

	public Boolean hasLocaleByLang(String language);

	public Boolean hasLocaleByScheduleAndLang(String schedule, String language);

	public Boolean hasQuote(Integer quoteId);

	public List<Quote> search(String term);

	public QuoteLocale setLocale(QuoteLocale locale);

	public Quote setQuote(Quote quote);

}
