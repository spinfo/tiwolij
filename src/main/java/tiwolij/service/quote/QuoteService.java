package tiwolij.service.quote;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Author;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;

public interface QuoteService {

	public Long count();

	public Long countByAuthor(Integer authorId);

	public Long countByWork(Integer workId);

	public Quote save(Quote quote);

	public void delete(Integer quoteId);

	public Quote getOneById(Integer quoteId);

	public Quote getRandomByLang(String language);

	public Quote getRandomByScheduleAndLang(String schedule, String language);

	public Quote getRandomNextBySibling(Integer quoteId);

	public Quote getRandomPrevBySibling(Integer quoteId);

	public List<Quote> getAll();

	public Page<Quote> getAll(Pageable pageable);

	public List<Quote> getAllByAuthor(Integer authorId);

	public Page<Quote> getAllByAuthor(Pageable pageable, Integer authorId);

	public List<Quote> getAllByWork(Integer workId);

	public Page<Quote> getAllByWork(Pageable pageable, Integer workId);

	public List<Quote> getAllBySchedule(String schedule);

	public Page<Quote> getAllBySchedule(Pageable pageable, String schedule);

	public List<Quote> getAllByScheduleAndLang(String schedule, String language);

	public Page<Quote> getAllByScheduleAndLang(Pageable pageable, String schedule, String language);

	public List<Quote> search(String term);

	public Page<Quote> search(Pageable pageable, String term);

	default public Long countByAuthor(Author author) {
		return countByAuthor(author.getId());
	}

	default public Long countByWork(Work work) {
		return countByWork(work.getId());
	}

	default public void delete(Quote quote) {
		delete(quote.getId());
	}

	default public Boolean existsById(Integer quoteId) {
		return (getOneById(quoteId) != null);
	}

	default public Boolean existsByLang(String language) {
		return (getRandomByLang(language) != null);
	}

	default public Boolean existsByScheduleAndLang(String schedule, String language) {
		return (getRandomByScheduleAndLang(schedule, language) != null);
	}

	default public List<Quote> getAllByAuthor(Author author) {
		return getAllByAuthor(author.getId());
	}

	default public Page<Quote> getAllByAuthor(Pageable pageable, Author author) {
		return getAllByAuthor(pageable, author.getId());
	}

	default public List<Quote> getAllByWork(Work work) {
		return getAllByWork(work.getId());
	}

	default public Page<Quote> getAllByWork(Pageable pageable, Work work) {
		return getAllByWork(pageable, work.getId());
	}

}
