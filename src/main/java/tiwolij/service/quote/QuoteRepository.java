package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.Quote;

public interface QuoteRepository extends PagingAndSortingRepository<Quote, Integer> {

	public Quote findTop1ById(Integer quoteId);

	public List<Quote> findAll();

	public List<Quote> findAllByWorkId(Integer workId);

	// pagination

	public Page<Quote> findAll(Pageable pageable);

	public Page<Quote> findAllByWorkId(Pageable pageable, Integer workId);

}
