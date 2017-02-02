package tiwolij.service.locale;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Locale;

@Component
@Transactional
public class LocaleServiceImpl implements LocaleService {

	private LocaleRepository locales;

	public LocaleServiceImpl(LocaleRepository locales) {
		this.locales = locales;
	}

	@Override
	public Long count() {
		return locales.count();
	}

	@Override
	public Long countByAuthors() {
		return locales.countByAuthors();
	}

	@Override
	public Long countByAuthor(Integer authorId) {
		return locales.countByAuthorId(authorId);
	}

	@Override
	public Long countByWorks() {
		return locales.countByWorks();
	}

	@Override
	public Long countByWork(Integer workId) {
		return locales.countByWorkId(workId);
	}

	@Override
	public Locale save(Locale locale) {
		return locales.save(locale);
	}

	@Override
	public void delete(Integer localeId) {
		locales.delete(localeId);
	}

	@Override
	public Locale getOneById(Integer localeId) {
		return locales.findOneById(localeId);
	}

	@Override
	public Locale getOneByAuthorAndLang(Integer authorId, String language) {
		return locales.findOneByAuthorIdAndLanguage(authorId, language);
	}

	@Override
	public Locale getOneByWorkAndLang(Integer workId, String language) {
		return locales.findOneByWorkIdAndLanguage(workId, language);
	}

	@Override
	public List<Locale> getAll() {
		return locales.findAll();
	}

	@Override
	public Page<Locale> getAll(Pageable pageable) {
		return locales.findAll(pageable);
	}

	@Override
	public List<Locale> getAllForAuthors() {
		return locales.findAllByWorkId(null);
	}

	@Override
	public Page<Locale> getAllForAuthors(Pageable pageable) {
		return locales.findAllByWorkId(pageable, null);
	}

	@Override
	public List<Locale> getAllForWorks() {
		return locales.findAllByAuthorId(null);
	}

	@Override
	public Page<Locale> getAllForWorks(Pageable pageable) {
		return locales.findAllByAuthorId(pageable, null);
	}

	@Override
	public List<Locale> getAllByAuthor(Integer authorId) {
		return locales.findAllByAuthorId(authorId);
	}

	@Override
	public Page<Locale> getAllByAuthor(Pageable pageable, Integer authorId) {
		return locales.findAllByAuthorId(pageable, authorId);
	}

	@Override
	public List<Locale> getAllByWork(Integer workId) {
		return locales.findAllByWorkId(workId);
	}

	@Override
	public Page<Locale> getAllByWork(Pageable pageable, Integer workId) {
		return locales.findAllByWorkId(pageable, workId);
	}

	@Override
	public Map<String, Locale> getMappedByAuthor(Integer authorId) {
		List<Locale> result = locales.findAllByAuthorId(authorId);
		return (result.isEmpty()) ? null : result.stream().collect(Collectors.toMap(Locale::getLanguage, i -> i));
	}

	@Override
	public Map<String, Locale> getMappedByWork(Integer workId) {
		List<Locale> result = locales.findAllByWorkId(workId);
		return (result.isEmpty()) ? null : result.stream().collect(Collectors.toMap(Locale::getLanguage, i -> i));
	}

}
