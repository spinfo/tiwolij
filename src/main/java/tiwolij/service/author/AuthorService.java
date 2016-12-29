package tiwolij.service.author;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;

public interface AuthorService {

	public Long count();

	public Long countLocales();

	public void delAuthor(Integer authorId);

	public void delLocale(Integer localeId);

	public Author getAuthor(Integer authorId);

	public Author getAuthorBySlug(String slug);

	public Author getAuthorByWikidataId(Integer wikidataId);

	public List<Author> getAuthors();

	public Page<Author> getAuthors(Pageable pageable);

	public AuthorLocale getLocale(Integer localeId);

	public AuthorLocale getLocaleByLang(Integer authorId, String language);

	public List<AuthorLocale> getLocales();

	public Page<AuthorLocale> getLocales(Pageable pageable);

	public List<AuthorLocale> getLocalesByAuthor(Integer authorId);

	public Page<AuthorLocale> getLocalesByAuthor(Pageable pageable, Integer authorId);

	public Boolean hasAuthor(Integer authorId);

	public Boolean hasAuthorBySlug(String slug);

	public Boolean hasAuthorByWikidataId(Integer wikidataId);

	public Boolean hasLocale(Integer authorId, String language);

	public List<Author> search(String term);

	public Author setAuthor(Author author);

	public AuthorLocale setLocale(AuthorLocale locale);
}
