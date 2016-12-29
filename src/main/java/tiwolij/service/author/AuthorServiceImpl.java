package tiwolij.service.author;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;

@Component
@Transactional
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authors;

	private final AuthorLocaleRepository locales;

	public AuthorServiceImpl(AuthorRepository authors, AuthorLocaleRepository locales) {
		this.authors = authors;
		this.locales = locales;
	}

	@Override
	public Long count() {
		return authors.count();
	}

	@Override
	public Long countLocales() {
		return locales.count();
	}

	@Override
	public void delAuthor(Integer authorId) {
		authors.delete(authorId);
	}

	@Override
	public void delLocale(Integer localeId) {

		locales.delete(localeId);
	}

	@Override
	public Author getAuthor(Integer authorId) {
		return authors.findTop1ById(authorId);
	}

	@Override
	public Author getAuthorBySlug(String slug) {
		return authors.findTop1BySlug(slug);
	}

	@Override
	public Author getAuthorByWikidataId(Integer wikidataId) {
		return authors.findTop1ByWikidataId(wikidataId);
	}

	@Override
	public List<Author> getAuthors() {
		return authors.findAll();
	}

	@Override
	public Page<Author> getAuthors(Pageable pageable) {
		return authors.findAll(pageable);
	}

	@Override
	public AuthorLocale getLocale(Integer localeId) {
		return locales.findTop1ById(localeId);
	}

	@Override
	public AuthorLocale getLocaleByLang(Integer authorId, String language) {
		if (!hasLocale(authorId, language))
			return null;

		return locales.findTop1ByAuthorIdAndLanguage(authorId, language);
	}

	@Override
	public List<AuthorLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public Page<AuthorLocale> getLocales(Pageable pageable) {
		return locales.findAll(pageable);
	}

	@Override
	public List<AuthorLocale> getLocalesByAuthor(Integer authorId) {
		return locales.findAllByAuthorId(authorId);
	}

	@Override
	public Page<AuthorLocale> getLocalesByAuthor(Pageable pageable, Integer authorId) {
		return locales.findAllByAuthorId(pageable, authorId);
	}

	@Override
	public Boolean hasAuthor(Integer authorId) {
		return authors.exists(authorId);
	}

	@Override
	public Boolean hasAuthorBySlug(String slug) {
		return authors.findTop1BySlug(slug) != null;
	}

	@Override
	public Boolean hasAuthorByWikidataId(Integer wikidataId) {
		return authors.findTop1ByWikidataId(wikidataId) != null;
	}

	@Override
	public Boolean hasLocale(Integer authorId, String language) {
		return locales.findTop1ByAuthorIdAndLanguage(authorId, language) != null;
	}

	@Override
	public List<Author> search(String term) {
		List<AuthorLocale> found = locales.findAllByNameContainingIgnoreCase(term);
		List<Author> result = new ArrayList<Author>();

		for (AuthorLocale l : found)
			if (!result.contains(l.getAuthor()))
				result.add(l.getAuthor());

		return result;
	}

	@Override
	public Author setAuthor(Author author) {
		return authors.save(author);
	}

	@Override
	public AuthorLocale setLocale(AuthorLocale locale) {
		return locales.save(locale);
	}

}
