package tiwolij.service.quote;

import java.util.List;

import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;

public interface QuoteService {

	/*
	 * GETTERS
	 */

	public Long getCount();

	public Quote getQuote(Integer quoteId);

	public List<Quote> getQuotes();

	public List<Quote> getQuotesByWork(Integer workId);

	public Long getLocaleCount();

	public QuoteLocale getLocale(Integer localeId);
	
	public QuoteLocale getLocaleByLang(Integer quoteId, String language);
	
	public QuoteLocale getLocaleByScheduleAndLang(String schedule, String language);

	public QuoteLocale getLocaleNextByScheduleAndLang(String schedule, String language) throws Exception;

	public QuoteLocale getLocalePrevByScheduleAndLang(String schedule, String language) throws Exception;
	
	public List<QuoteLocale> getLocales();

	public List<QuoteLocale> getLocalesByQuote(Integer quoteId);

	/*
	 * SETTERS
	 */

	public Quote setQuote(Quote quote);

	public QuoteLocale setLocale(QuoteLocale locale);

	/*
	 * DELETERS
	 */

	public void delQuote(Integer quoteId);

	public void delLocale(Integer localeId);

	/*
	 * CHECKERS
	 */

	public Boolean hasQuote(Integer quoteId);

	public Boolean hasLocale(Integer quoteId, String language);

}
