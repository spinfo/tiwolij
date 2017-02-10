package tiwolij.service.locale;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Locale;

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

	default public Boolean existsById(Integer localeId) {
		return (localeId != null && getOneById(localeId) != null);
	}

	default public Boolean existsByAuthorAndLang(Integer authorId, String language) {
		return (authorId != null && language != null && getOneByAuthorAndLang(authorId, language) != null);
	}

	default public Boolean existsByWorkAndLang(Integer workId, String language) {
		return (workId != null && language != null && getOneByWorkAndLang(workId, language) != null);
	}

}
