package tiwolij.service.work;

import java.net.URL;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.NoSuchEntityErrorException;

import tiwolij.domain.Author;
import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.author.AuthorService;

@Component
@Transactional
public class WorkServiceImpl implements WorkService {
	private final Environment env;

	private final AuthorService authors;

	private final WorkRepository works;

	private final WorkLocaleRepository locales;

	private final Pattern regexLang;
	private final Pattern regexTitle;
	private final Pattern regexWDId;

	public WorkServiceImpl(Environment env, AuthorService authors, WorkRepository works, WorkLocaleRepository locales) {
		this.env = env;
		this.authors = authors;
		this.works = works;
		this.locales = locales;

		regexLang = Pattern.compile(env.getProperty("tiwolij.import.regex.lang"));
		regexTitle = Pattern.compile(env.getProperty("tiwolij.import.regex.title"));
		regexWDId = Pattern.compile(env.getProperty("tiwolij.import.regex.wdid"));
	}

	/*
	 * GETTERS
	 */

	@Override
	public Work getWork(Integer workId) {
		return works.findOneById(workId);
	}

	@Override
	public Work getWorkBySlug(String slug) {
		return works.findOneBySlug(slug);
	}

	@Override
	public Work getWorkByWikidataId(Integer wikidataId) {
		return works.findOneByWikidataId(wikidataId);
	}

	@Override
	public List<Work> getWorks() {
		return works.findAll();
	}

	@Override
	public List<Work> getWorksByAuthor(Integer authorId) {
		return works.findAllByAuthorId(authorId);
	}

	@Override
	public WorkLocale getLocale(Integer localeId) {
		return locales.findOneById(localeId);
	}

	@Override
	public WorkLocale getLocaleByLang(Integer workId, String language) {
		if (!hasLocale(workId, language))
			return null;

		return locales.findOneByWorkIdAndLanguage(workId, language);
	}

	@Override
	public List<WorkLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public List<WorkLocale> getLocalesByWork(Integer workId) {
		return locales.findAllByWorkId(workId);
	}

	/*
	 * SETTERS
	 */

	@Override
	public Work setWork(Work work) {
		return works.save(work);
	}

	@Override
	public WorkLocale setLocale(WorkLocale locale) {
		return locales.save(locale);
	}

	/*
	 * DELETERS
	 */

	@Override
	public void delWork(Integer workId) {
		works.delete(workId);
	}

	@Override
	public void delLocale(Integer localeId) {
		locales.delete(localeId);
	}

	/*
	 * CHECKERS
	 */

	@Override
	public Boolean hasWork(Integer workId) {
		return works.exists(workId);
	}

	@Override
	public Boolean hasWorkBySlug(String slug) {
		return works.findOneBySlug(slug) != null;
	}

	@Override
	public Boolean hasWorkByWikidataId(Integer wikidataId) {
		return works.findOneByWikidataId(wikidataId) != null;
	}

	@Override
	public Boolean hasLocale(Integer workId, String language) {
		return locales.findOneByWorkIdAndLanguage(workId, language) != null;
	}

	/*
	 * COUNTERS
	 */

	@Override
	public Long count() {
		return works.count();
	}

	@Override
	public Long countByAuthorId(Integer authorId) {
		return new Long(works.findAllByAuthorId(authorId).size());
	}

	@Override
	public Long countLocales() {
		return locales.count();
	}

	/*
	 * IMPORTERS
	 */

	@Override
	public Work importWorkByWikidataId(Integer wikidataId) throws Exception {
		if (hasWorkByWikidataId(wikidataId))
			return works.findOneByWikidataId(wikidataId);

		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

		Work work = new Work();
		work.setWikidataId(wikidataId);

		if (item.getLabels().containsKey("en"))
			work.setSlug(item.getLabels().get("en").getText().replace(" ", "_"));
		else
			for (String lang : env.getProperty("tiwolij.localizations", String[].class))
				if (item.getLabels().containsKey(lang))
					work.setSlug(item.getLabels().get(lang).getText().replace(" ", "_"));

		if (!item.hasStatement("P50"))
			return work;

		String p50 = item.findStatementGroup("P50").getStatements().get(0).getValue().toString();
		Matcher wikiData = regexWDId.matcher(p50);

		if (!wikiData.find())
			throw new NoSuchEntityErrorException("Q" + wikidataId + " has no author");

		Integer id = Integer.parseInt(wikiData.group(1));

		if (!authors.hasAuthorByWikidataId(id))
			authors.importAuthorByWikidataId(id);

		Author author = authors.getAuthorByWikidataId(id);
		work = works.save(work.setAuthor(author));

		return work;
	}

	@Override
	public Work importWorkByArticle(String article) throws Exception {
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

		return hasWorkByWikidataId(wikidataId) ? getWorkByWikidataId(wikidataId) : importWorkByWikidataId(wikidataId);
	}

	@Override
	public WorkLocale importLocale(Integer workId, String language) throws Exception {
		Work work = getWork(workId);
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + work.getWikidataId());
		Map<String, MonolingualTextValue> labels = item.getLabels();
		MonolingualTextValue label = labels.get(language);

		if (label == null)
			throw new NoSuchEntityErrorException(
					"Q" + work.getWikidataId() + " has no locale for language " + language);

		WorkLocale locale = new WorkLocale(work);
		locale.setLanguage(language);
		locale.setName(label.getText());
		locale.setHref("https://" + language + ".wikipedia.org/wiki/" + label.getText().replace(" ", "_"));

		return locales.save(locale);
	}

	@Override
	public List<WorkLocale> importLocales(Integer workId) {
		for (String language : env.getProperty("tiwolij.localizations", String[].class)) {
			try {
				importLocale(workId, language);
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}

		return getLocalesByWork(workId);
	}

}
