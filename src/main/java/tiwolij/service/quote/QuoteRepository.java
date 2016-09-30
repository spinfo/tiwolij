package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Quote;

public interface QuoteRepository extends CrudRepository<Quote, Integer> {

	public Quote findOneById(Integer quoteId);
	
	public List<Quote> findAll();

	public List<Quote> findAllByWorkId(Integer workId);

}
