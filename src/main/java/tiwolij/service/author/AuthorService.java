package tiwolij.service.author;

import java.net.URL;
import java.util.List;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;

public interface AuthorService {

	/*
	 * GETTERS
	 */

	public Long getCount();

	public Author getAuthor(Integer authorId);

	public Author getAuthorByWikidata(Integer wikidataId);

	public List<Author> getAuthors();

	public Long getLocaleCount();

	public AuthorLocale getLocale(Integer localeId);

	public AuthorLocale getLocaleByLang(Integer authorId, String language);

	public List<AuthorLocale> getLocales();

	public List<AuthorLocale> getLocalesByAuthor(Integer authorId);

	/*
	 * SETTERS
	 */

	public Author setAuthor(Author author);

	public AuthorLocale setLocale(AuthorLocale locale);

	/*
	 * DELETERS
	 */

	public void delAuthor(Integer authorId);

	public void delLocale(Integer localeId);

	/*
	 * CHECKERS
	 */

	public Boolean hasAuthor(Integer authorId);

	public Boolean hasLocale(Integer authorId, String language);

	/*
	 * IMPORTERS
	 */

	public Author importAuthor(Integer wikidataId) throws Exception;

	public Author importImage(Integer authorId, URL url) throws Exception;

	public Author importImageAttribution(Integer authorId, String image) throws Exception;

	public AuthorLocale importLocale(Integer authorId, String language) throws Exception;

	public List<AuthorLocale> importLocales(Integer authorId) throws Exception;

}
