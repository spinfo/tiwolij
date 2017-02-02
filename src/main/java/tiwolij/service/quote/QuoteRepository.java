package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.Quote;

public interface QuoteRepository extends PagingAndSortingRepository<Quote, Integer> {

	public Long countByWorkAuthorId(Integer authorId);

	public Long countByWorkId(Integer workId);

	public Quote findOneById(Integer quoteId);

	public List<Quote> findAll();

	public Page<Quote> findAll(Pageable pageable);

	public List<Quote> findAllByWorkAuthorId(Integer authorId);

	public Page<Quote> findAllByWorkAuthorId(Pageable pageable, Integer authorId);

	public List<Quote> findAllByWorkId(Integer workId);

	public Page<Quote> findAllByWorkId(Pageable pageable, Integer workId);

	public List<Quote> findAllBySchedule(String schedule);

	public Page<Quote> findAllBySchedule(Pageable pageable, String schedule);

	public List<Quote> findAllByLanguage(String language);

	public Page<Quote> findAllByLanguage(Pageable pageable, String language);

	public List<Quote> findAllByScheduleAndLanguage(String schedule, String language);

	public Page<Quote> findAllByScheduleAndLanguage(Pageable pageable, String schedule, String language);

	public List<Quote> findAllByCorpusContainingIgnoreCase(String term);

	public Page<Quote> findAllByCorpusContainingIgnoreCase(Pageable pageable, String term);

}
