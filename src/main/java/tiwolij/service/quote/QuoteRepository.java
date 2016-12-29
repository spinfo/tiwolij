package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.Quote;

public interface QuoteRepository extends PagingAndSortingRepository<Quote, Integer> {

	public List<Quote> findAll();

	public Page<Quote> findAll(Pageable pageable);

	public List<Quote> findAllByWorkId(Integer workId);

	public Page<Quote> findAllByWorkId(Pageable pageable, Integer workId);

	public Quote findTop1ById(Integer quoteId);

}
