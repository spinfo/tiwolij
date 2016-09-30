package tiwolij.service.author;

import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;

@Component
@Transactional
public class AuthorServiceImpl implements AuthorService {

	private final Environment env;

	private final AuthorRepository authors;

	private final AuthorLocaleRepository locales;

	public AuthorServiceImpl(Environment env, AuthorRepository authors, AuthorLocaleRepository locales) {
		this.env = env;
		this.authors = authors;
		this.locales = locales;
	}

	/*
	 * GETTERS
	 */

	@Override
	public Author getAuthor(Integer authorId) {
		Assert.notNull(authorId);

		return authors.findOne(authorId);
	}

	@Override
	public Author getAuthorByWikidata(Integer wikidataId) {
		Assert.notNull(wikidataId);

		return authors.findOneByWikidataId(wikidataId);
	}

	@Override
	public List<Author> getAuthors() {
		return authors.findAll();
	}

	@Override
	public AuthorLocale getLocale(Integer localeId) {
		Assert.notNull(localeId);

		return locales.findOne(localeId);
	}

	@Override
	public List<AuthorLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public List<AuthorLocale> getLocalesByAuthor(Integer authorId) {
		Assert.notNull(authorId);

		return locales.findAllByAuthorId(authorId);
	}

	/*
	 * SETTERS
	 */

	@Override
	public Author setAuthor(Author author) {
		Assert.notNull(author);

		return authors.save(author);
	}

	@Override
	public AuthorLocale setLocale(AuthorLocale locale) {
		Assert.notNull(locale);
		
		return locales.save(locale);
	}

	/*
	 * DELETERS
	 */

	@Override
	public void delAuthor(Integer authorId) {
		Assert.notNull(authorId);

		authors.delete(authorId);
	}

	@Override
	public void delLocale(Integer localeId) {
		Assert.notNull(localeId);
		
		locales.delete(localeId);
	}

	/*
	 * CHECKERS
	 */

	@Override
	public Boolean hasAuthor(Integer authorId) {
		return authors.exists(authorId);
	}

	@Override
	public Boolean hasLocale(Integer authorId, String language) {
		Assert.notNull(authorId);

		return locales.findAllByAuthorId(authorId).stream().filter(o -> o.getLanguage().equals(language)).findFirst()
				.isPresent();
	}

	/*
	 * IMPORTERS
	 */

	@Override
	public Author importAuthor(Integer wikidataId) throws Exception {
		Assert.notNull(wikidataId);

		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

		Author author = new Author();
		author.setSlug(item.getLabels().get("en").getText().replace(" ", "_"));
		author.setWikidataId(wikidataId);
		author = authors.save(author);

		if (item.hasStatement("P18")) {
			String name = item.findStatementStringValue("P18").getString().replace(" ", "_");
			String md5 = DigestUtils.md5Hex(name);
			String dir = md5.substring(0, 1) + "/" + md5.substring(0, 2) + "/";
			URL url = new URL("https://upload.wikimedia.org/wikipedia/commons/" + dir + name);

			author = importImage(author.getId(), url);

			// TODO: weiter testen ob imageAttribution Import funtioniert

			URL commons = new URL("https://tools.wmflabs.org/magnus-toolserver/commonsapi.php?image=" + name);
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = builder.parse(commons.openStream());

			if (document.getElementsByTagName("attribute_author").getLength() > 0) {
				NodeList attribution = document.getElementsByTagName("author");
				NodeList licenses = document.getElementsByTagName("license");

				for (Integer i = 0; i < attribution.getLength(); i++)
					System.out.println(attribution.item(i).getTextContent());

				for (Integer i = 0; i < licenses.getLength(); i++)
					System.out.println(licenses.item(i).getTextContent());
			}
		}

		importLocales(author.getId());

		return author;
	}

	@Override
	public Author importImage(Integer authorId, URL url) throws Exception {
		Assert.notNull(authorId);
		Assert.notNull(url);

		Author author = getAuthor(authorId);
		author.setImage(IOUtils.toByteArray(url.openStream()));

		return authors.save(author);
	}

	@Override
	public AuthorLocale importLocale(Integer authorId, String language) throws Exception {
		Assert.notNull(authorId);
		Assert.notNull(language);

		Author author = getAuthor(authorId);
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + author.getWikidataId());
		Map<String, MonolingualTextValue> labels = item.getLabels();
		MonolingualTextValue label = labels.get(language);

		AuthorLocale locale = new AuthorLocale(author);
		locale.setLanguage(language);
		locale.setName(label.getText());
		locale.setHref("https://" + language + ".wikipedia.org/wiki/" + label.getText().replace(" ", "_"));

		return locales.save(locale);
	}

	@Override
	public void importLocales(Integer authorId) throws Exception {
		Assert.notNull(authorId);

		for (String language : env.getProperty("tiwolij.localizations").split(", "))
			importLocale(authorId, language);
	}

}
