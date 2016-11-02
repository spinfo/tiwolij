package tiwolij.service.quote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.RecordId;

@Component
@Transactional
public class QuoteServiceImpl implements QuoteService {
	private final QuoteRepository quotes;

	private final QuoteLocaleRepository locales;

	public QuoteServiceImpl(QuoteRepository quotes, QuoteLocaleRepository locales) {
		this.quotes = quotes;
		this.locales = locales;
	}

	/*
	 * GETTERS
	 */

	@Override
	public Long getCount() {
		return quotes.count();
	}

	@Override
	public Quote getQuote(Integer quoteId) {
		return quotes.findTop1ById(quoteId);
	}

	@Override
	public List<Quote> getQuotes() {
		return quotes.findAll();
	}

	@Override
	public List<Quote> getQuotesByWork(Integer workId) {
		return quotes.findAllByWorkId(workId);
	}

	@Override
	public Long getLocaleCount() {
		return locales.count();
	}

	@Override
	public QuoteLocale getLocale(Integer localeId) {
		return locales.findTop1ById(localeId);
	}

	@Override
	public QuoteLocale getLocaleByQuoteAndLang(Integer quoteId, String language) {
		if (!hasLocale(quoteId, language))
			return null;

		return locales.findTop1ByQuoteIdAndLanguage(quoteId, language);
	}

	@Override
	public QuoteLocale getLocaleByScheduleAndLang(String schedule, String language) {
		return locales.findTop1ByScheduleAndLanguage(schedule, language);
	}

	@Override
	public QuoteLocale getLocaleRandomByLang(String language) {
		List<RecordId> list = locales.findAllByLanguage(language);
		Collections.shuffle(list);

		return locales.findTop1ById(list.get(0).getId());
	}

	@Override
	public QuoteLocale getLocaleNextByScheduleAndLang(String schedule, String language, Boolean prev) throws Exception {
		QuoteLocale result = null;
		Calendar cal = Calendar.getInstance();
		DateFormat format = new SimpleDateFormat("dd-MM-yyyy");

		cal.setTime(format.parse(schedule + "-" + cal.get(Calendar.YEAR)));

		while (result == null) {
			cal.add(Calendar.DATE, prev ? -1 : +1);
			result = locales.findTop1ByScheduleAndLanguage(format.format(cal.getTime()).substring(0, 5), language);
		}

		return result;
	}

	@Override
	public List<QuoteLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public List<QuoteLocale> getLocalesByQuote(Integer quoteId) {
		return locales.findAllByQuoteId(quoteId);
	}

	@Override
	public List<QuoteLocale> getLocalesByScheduleAndLang(String schedule, String language) {
		return locales.findAllByScheduleAndLanguage(schedule, language);
	}

	/*
	 * SETTERS
	 */

	@Override
	public Quote setQuote(Quote quote) {
		return quotes.save(quote);
	}

	@Override
	public QuoteLocale setLocale(QuoteLocale locale) {
		return locales.save(locale);
	}

	/*
	 * DELETERS
	 */

	@Override
	public void delQuote(Integer quoteId) {
		quotes.delete(quoteId);
	}

	@Override
	public void delLocale(Integer localeId) {
		locales.delete(localeId);
	}

	/*
	 * CHECKERS
	 */

	@Override
	public Boolean hasQuote(Integer quoteId) {
		return quotes.exists(quoteId);
	}

	@Override
	public Boolean hasLocale(Integer quoteId, String language) {
		return locales.findTop1ByQuoteIdAndLanguage(quoteId, language) != null;
	}

	@Override
	public Boolean hasLocaleByScheduleAndLang(String schedule, String language) {
		return locales.findTop1ByScheduleAndLanguage(schedule, language) != null;
	}

}
