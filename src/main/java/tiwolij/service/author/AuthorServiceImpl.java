package tiwolij.service.author;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.NoSuchEntityErrorException;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;

@Component
@Transactional
public class AuthorServiceImpl implements AuthorService {

	private final Environment env;

	private final AuthorRepository authors;

	private final AuthorLocaleRepository locales;

	private final Pattern regexLang;
	private final Pattern regexTitle;

	public AuthorServiceImpl(Environment env, AuthorRepository authors, AuthorLocaleRepository locales) {
		this.env = env;
		this.authors = authors;
		this.locales = locales;

		regexLang = Pattern.compile(env.getProperty("tiwolij.import.regex.lang"));
		regexTitle = Pattern.compile(env.getProperty("tiwolij.import.regex.title"));
	}

	/*
	 * GETTERS
	 */

	@Override
	public Author getAuthor(Integer authorId) {
		return authors.findOneById(authorId);
	}

	@Override
	public Author getAuthorBySlug(String slug) {
		return authors.findOneBySlug(slug);
	}

	@Override
	public Author getAuthorByWikidataId(Integer wikidataId) {
		return authors.findOneByWikidataId(wikidataId);
	}

	@Override
	public List<Author> getAuthors() {
		return authors.findAll();
	}

	@Override
	public AuthorLocale getLocale(Integer localeId) {
		return locales.findOneById(localeId);
	}

	@Override
	public AuthorLocale getLocaleByLang(Integer authorId, String language) {
		if (!hasLocale(authorId, language))
			return null;

		return locales.findOneByAuthorIdAndLanguage(authorId, language);
	}

	@Override
	public List<AuthorLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public List<AuthorLocale> getLocalesByAuthor(Integer authorId) {
		return locales.findAllByAuthorId(authorId);
	}

	/*
	 * SETTERS
	 */

	@Override
	public Author setAuthor(Author author) {
		return authors.save(author);
	}

	@Override
	public AuthorLocale setLocale(AuthorLocale locale) {
		return locales.save(locale);
	}

	/*
	 * DELETERS
	 */

	@Override
	public void delAuthor(Integer authorId) {
		authors.delete(authorId);
	}

	@Override
	public void delLocale(Integer localeId) {

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
	public Boolean hasAuthorBySlug(String slug) {
		return authors.findOneBySlug(slug) != null;
	}

	@Override
	public Boolean hasAuthorByWikidataId(Integer wikidataId) {
		return authors.findOneByWikidataId(wikidataId) != null;
	}

	@Override
	public Boolean hasLocale(Integer authorId, String language) {
		return locales.findOneByAuthorIdAndLanguage(authorId, language) != null;
	}

	/*
	 * COUNTERS
	 */

	@Override
	public Long count() {
		return authors.count();
	}

	@Override
	public Long countLocales() {
		return locales.count();
	}

	/*
	 * IMPORTERS
	 */

	@Override
	public Author importAuthorByWikidataId(Integer wikidataId) throws Exception {
		if (hasAuthorByWikidataId(wikidataId))
			return authors.findOneByWikidataId(wikidataId);

		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

		Author author = new Author();
		author.setWikidataId(wikidataId);

		if (item.getLabels().containsKey("en"))
			author.setSlug(item.getLabels().get("en").getText().replace(" ", "_"));
		else
			for (String lang : env.getProperty("tiwolij.localizations", String[].class))
				if (item.getLabels().containsKey(lang))
					author.setSlug(item.getLabels().get(lang).getText().replace(" ", "_"));

		author = authors.save(author);

		if (item.hasStatement("P18")) {
			Value value = item.findStatementGroup("P18").getStatements().get(0).getValue();
			String image = value.toString().replaceAll("^\"|\"$", "").replace(" ", "_");
			String md5 = DigestUtils.md5Hex(image);
			String dir = md5.substring(0, 1) + "/" + md5.substring(0, 2) + "/";
			URL url = new URL("https://upload.wikimedia.org/wikipedia/commons/" + dir + image);

			author = importImage(author.getId(), url);
			author = importImageAttribution(author.getId(), image);
		}

		return author;
	}

	@Override
	public Author importAuthorByArticle(String article) throws Exception {
		Matcher wikiTitle = regexTitle.matcher(article);
		Matcher wikiLanguage = regexLang.matcher(article);

		if (!wikiTitle.find() || !wikiLanguage.find())
			throw new ParseException(article, 0);

		String title = wikiTitle.group(1);
		String language = wikiLanguage.group(1);

		String api = env.getProperty("wikipedia.api.article.wikidataid");
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(new URL("https://" + language + api + title).openStream());

		Node props = document.getElementsByTagName("pageprops").item(0);
		String id = props.getAttributes().getNamedItem("wikibase_item").getNodeValue();
		Integer wikidataId = Integer.parseInt(id.substring(1));

		return hasAuthorByWikidataId(wikidataId) ? getAuthorByWikidataId(wikidataId)
				: importAuthorByWikidataId(wikidataId);
	}

	@Override
	public Author importImage(Integer authorId, URL url) throws Exception {
		Author author = getAuthor(authorId);

		// https://stackoverflow.com/questions/1228381
		byte[] bytes = IOUtils.toByteArray(url.openStream());
		BufferedImage input = ImageIO.read(new ByteArrayInputStream(bytes));
		
		if (input == null)
			return author;
		
		Integer height = Integer.parseInt(env.getProperty("tiwolij.import.image.height"));
		Integer width = (height * input.getWidth()) / input.getHeight();

		Image output = input.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.getGraphics().drawImage(output, 0, 0, new Color(0, 0, 0), null);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", stream);
		author.setImage(stream.toByteArray());

		return authors.save(author);
	}

	@Override
	public Author importImageAttribution(Integer authorId, String image) throws Exception {
		URL wiki = new URL("https://" + env.getProperty("wikimedia.api.image.extinfo") + image);
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = builder.parse(wiki.openStream());

		Node creatorNode = document.getElementsByTagName("Artist").item(0);
		Node licenseNode = document.getElementsByTagName("LicenseShortName").item(0);

		String creator = (creatorNode == null) ? "Unknown Artist"
				: creatorNode.getAttributes().getNamedItem("value").getNodeValue().replaceAll("\\<[^>]*>", "");
		String license = (licenseNode == null) ? "Unspecified License"
				: licenseNode.getAttributes().getNamedItem("value").getNodeValue().replaceAll("\\<[^>]*>", "");

		Author author = getAuthor(authorId);
		author.setImageAttribution(creator + " (" + license + ")");

		return authors.save(author);
	}

	@Override
	public AuthorLocale importLocale(Integer authorId, String language) throws Exception {
		Author author = getAuthor(authorId);
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + author.getWikidataId());
		Map<String, MonolingualTextValue> labels = item.getLabels();
		MonolingualTextValue label = labels.get(language);

		if (label == null)
			throw new NoSuchEntityErrorException(
					"Q" + author.getWikidataId() + " has no locale for language " + language);

		AuthorLocale locale = new AuthorLocale(author);
		locale.setLanguage(language);
		locale.setName(label.getText());
		locale.setHref("https://" + language + ".wikipedia.org/wiki/" + label.getText().replace(" ", "_"));

		return locales.save(locale);
	}

	@Override
	public List<AuthorLocale> importLocales(Integer authorId) {
		for (String language : env.getProperty("tiwolij.localizations", String[].class)) {
			try {
				importLocale(authorId, language);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		return locales.findAllByAuthorId(authorId);
	}

}
