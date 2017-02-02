package tiwolij.service.quote;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Quote;

@Component
@Transactional
public class QuoteServiceImpl implements QuoteService {

	private final QuoteRepository quotes;

	public QuoteServiceImpl(QuoteRepository quotes) {
		this.quotes = quotes;
	}

	@Override
	public Long count() {
		return quotes.count();
	}

	@Override
	public Long countByAuthor(Integer authorId) {
		return quotes.countByWorkAuthorId(authorId);
	}

	@Override
	public Long countByWork(Integer workId) {
		return quotes.countByWorkId(workId);
	}

	@Override
	public Quote save(Quote quote) {
		return quotes.save(quote);
	}

	@Override
	public void delete(Integer quoteId) {
		quotes.delete(quoteId);
	}

	@Override
	public Quote getOneById(Integer quoteId) {
		return quotes.findOneById(quoteId);
	}

	@Override
	public Quote getRandomByLang(String language) {
		List<Quote> list = quotes.findAllByLanguage(language);
		Collections.shuffle(list);

		return (list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public Quote getRandomByScheduleAndLang(String schedule, String language) {
		List<Quote> list = quotes.findAllByScheduleAndLanguage(schedule, language);
		Collections.shuffle(list);

		return (list.size() > 0) ? list.get(0) : null;
	}

	@Override
	public Quote getRandomNextBySibling(Integer quoteId) {
		Quote quote = getOneById(quoteId);
		Quote sibling = null;

		try {
			Calendar cal = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			cal.setTime(format.parse(quote.getSchedule() + "-" + cal.get(Calendar.YEAR)));

			while (sibling == null) {
				cal.add(Calendar.DATE, 1);
				String schedule = format.format(cal.getTime()).substring(0, 5);

				sibling = getRandomByScheduleAndLang(schedule, quote.getLanguage());
			}
		} catch (Exception e) {
		}

		return sibling;
	}

	@Override
	public Quote getRandomPrevBySibling(Integer quoteId) {
		Quote quote = getOneById(quoteId);
		Quote sibling = null;

		try {
			Calendar cal = Calendar.getInstance();
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			cal.setTime(format.parse(quote.getSchedule() + "-" + cal.get(Calendar.YEAR)));

			while (sibling == null) {
				cal.add(Calendar.DATE, -1);
				String schedule = format.format(cal.getTime()).substring(0, 5);

				sibling = getRandomByScheduleAndLang(schedule, quote.getLanguage());
			}
		} catch (Exception e) {
		}

		return sibling;
	}

	@Override
	public List<Quote> getAll() {
		return quotes.findAll();
	}

	@Override
	public Page<Quote> getAll(Pageable pageable) {
		return quotes.findAll(pageable);
	}

	@Override
	public List<Quote> getAllByAuthor(Integer authorId) {
		return quotes.findAllByWorkAuthorId(authorId);
	}

	@Override
	public Page<Quote> getAllByAuthor(Pageable pageable, Integer authorId) {
		return quotes.findAllByWorkAuthorId(pageable, authorId);
	}

	@Override
	public List<Quote> getAllByWork(Integer workId) {
		return quotes.findAllByWorkId(workId);
	}

	@Override
	public Page<Quote> getAllByWork(Pageable pageable, Integer workId) {
		return quotes.findAllByWorkId(pageable, workId);
	}

	@Override
	public List<Quote> getAllBySchedule(String schedule) {
		return quotes.findAllBySchedule(schedule);
	}

	@Override
	public Page<Quote> getAllBySchedule(Pageable pageable, String schedule) {
		return quotes.findAllBySchedule(pageable, schedule);
	}

	@Override
	public List<Quote> getAllByScheduleAndLang(String schedule, String language) {
		return quotes.findAllByScheduleAndLanguage(schedule, language);
	}

	@Override
	public Page<Quote> getAllByScheduleAndLang(Pageable pageable, String schedule, String language) {
		return quotes.findAllByScheduleAndLanguage(pageable, schedule, language);
	}

	@Override
	public List<Quote> search(String term) {
		return quotes.findAllByCorpusContainingIgnoreCase(term);
	}

	@Override
	public Page<Quote> search(Pageable pageable, String term) {
		return quotes.findAllByCorpusContainingIgnoreCase(pageable, term);
	}

}
