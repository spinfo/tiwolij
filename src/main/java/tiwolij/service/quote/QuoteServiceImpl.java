package tiwolij.service.quote;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;

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
		Assert.notNull(quoteId);

		return quotes.findOneById(quoteId);
	}

	@Override
	public List<Quote> getQuotes() {
		return quotes.findAll();
	}

	@Override
	public List<Quote> getQuotesByWork(Integer workId) {
		Assert.notNull(workId);

		return quotes.findAllByWorkId(workId);
	}

	@Override
	public Long getLocaleCount() {
		return locales.count();
	}
	
	@Override
	public QuoteLocale getLocale(Integer localeId) {
		Assert.notNull(localeId);

		return locales.findOneById(localeId);
	}

	@Override
	public List<QuoteLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public List<QuoteLocale> getLocalesByQuote(Integer quoteId) {
		Assert.notNull(quoteId);

		return locales.findAll();
	}

	/*
	 * SETTERS
	 */

	@Override
	public Quote setQuote(Quote quote) {
		Assert.notNull(quote);

		return quotes.save(quote);
	}

	@Override
	public QuoteLocale setLocale(QuoteLocale locale) {
		Assert.notNull(locale);

		return locales.save(locale);
	}

	/*
	 * DELETERS
	 */

	@Override
	public void delQuote(Integer quoteId) {
		Assert.notNull(quoteId);

		quotes.delete(quoteId);
	}

	@Override
	public void delLocale(Integer localeId) {
		Assert.notNull(localeId);

		locales.delete(localeId);
	}

	/*
	 * CHECKERS
	 */

	@Override
	public Boolean hasQuote(Integer quoteId) {
		Assert.notNull(quoteId);

		return quotes.exists(quoteId);
	}

	@Override
	public Boolean hasLocale(Integer quoteId, String language) {
		Assert.notNull(quoteId);
		Assert.notNull(language);

		return locales.findAllByQuoteId(quoteId).stream().filter(o -> o.getLanguage().equals(language)).findFirst()
				.isPresent();
	}

}
