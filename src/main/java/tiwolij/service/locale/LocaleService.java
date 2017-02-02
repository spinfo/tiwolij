package tiwolij.service.locale;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Work;

public interface LocaleService {

	public Long count();

	public Long countByAuthors();
	
	public Long countByAuthor(Integer authorId);

	public Long countByWorks();
	
	public Long countByWork(Integer workId);

	public Locale save(Locale locale);

	public void delete(Integer localeId);

	public Locale getOneById(Integer localeId);

	public Locale getOneByAuthorAndLang(Integer authorId, String language);

	public Locale getOneByWorkAndLang(Integer workId, String language);

	public List<Locale> getAll();

	public Page<Locale> getAll(Pageable pageable);
	
	public List<Locale> getAllForAuthors();

	public Page<Locale> getAllForAuthors(Pageable pageable);	

	public List<Locale> getAllForWorks();

	public Page<Locale> getAllForWorks(Pageable pageable);	
	
	public List<Locale> getAllByAuthor(Integer authorId);

	public Page<Locale> getAllByAuthor(Pageable pageable, Integer authorId);

	public List<Locale> getAllByWork(Integer workId);

	public Page<Locale> getAllByWork(Pageable pageable, Integer workId);

	public Map<String, Locale> getMappedByAuthor(Integer authorId);

	public Map<String, Locale> getMappedByWork(Integer workId);

	default public Long countByAuthor(Author author) {
		return countByAuthor(author.getId());
	}

	default public Long countByWork(Work work) {
		return countByWork(work.getId());
	}

	default public void delete(Locale locale) {
		delete(locale.getId());
	}

	default public Boolean existsById(Integer localeId) {
		return (getOneById(localeId) != null);
	}

	default public Boolean existsByAuthorAndLang(Integer authorId, String language) {
		return (getOneByAuthorAndLang(authorId, language) != null);
	}

	default public Boolean existsByWorkAndLang(Integer workId, String language) {
		return (getOneByWorkAndLang(workId, language) != null);
	}

	default public Boolean existsByAuthorAndLang(Author author, String language) {
		return existsByAuthorAndLang(author.getId(), language);
	}

	default public Boolean existsByWorkAndLang(Work work, String language) {
		return existsByWorkAndLang(work.getId(), language);
	}

	default public Locale getOneByAuthorAndLang(Author author, String language) {
		return getOneByAuthorAndLang(author.getId(), language);
	}

	default public Locale getOneByWorkAndLang(Work work, String language) {
		return getOneByWorkAndLang(work.getId(), language);
	}

	default public List<Locale> getAllByAuthor(Author author) {
		return getAllByAuthor(author.getId());
	}

	default public Page<Locale> getAllByAuthor(Pageable pageable, Author author) {
		return getAllByAuthor(pageable, author.getId());
	}

	default public List<Locale> getAllByWork(Work work) {
		return getAllByWork(work.getId());
	}

	default public Page<Locale> getAllByWork(Pageable pageable, Work work) {
		return getAllByWork(pageable, work.getId());
	}

	default public Map<String, Locale> getMappedByAuthor(Author author) {
		return getMappedByAuthor(author.getId());
	}

	default public Map<String, Locale> getMappedByWork(Work work) {
		return getMappedByWork(work.getId());
	}

}
