package tiwolij.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;

public interface QuoteLocaleRepository extends CrudRepository<QuoteLocale, Integer> {

	public QuoteLocale findById(int id);

	public List<QuoteLocale> findByQuote(Quote quote);
	
	public List<QuoteLocale> findAll();

}
