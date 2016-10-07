package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.QuoteLocale;

public interface QuoteLocaleRepository extends CrudRepository<QuoteLocale, Integer> {

	public QuoteLocale findOneById(Integer localeId);

	public QuoteLocale findOneByQuoteIdAndLanguage(Integer quoteId, String language);
	
	public List<QuoteLocale> findAll();
	
	public List<QuoteLocale> findAllByQuoteId(Integer quoteId);

}