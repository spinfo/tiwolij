package tiwolij.service.work;

import java.util.List;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
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

	public WorkServiceImpl(Environment env, AuthorService authors, WorkRepository works, WorkLocaleRepository locales) {
		this.env = env;
		this.authors = authors;
		this.works = works;
		this.locales = locales;
	}

	/*
	 * GETTERS
	 */

	@Override
	public Long getCount() {
		return works.count();
	}

	@Override
	public Work getWork(Integer workId) {
		Assert.notNull(workId);

		return works.findOneById(workId);
	}

	@Override
	public Work getWorkByWikidata(Integer wikidataId) {
		Assert.notNull(wikidataId);

		return works.findOneByWikidataId(wikidataId);
	}

	@Override
	public List<Work> getWorks() {
		return works.findAll();
	}

	@Override
	public List<Work> getWorksByAuthor(Integer authorId) {
		Assert.notNull(authorId);

		return works.findAllByAuthorId(authorId);
	}

	@Override
	public Long getLocaleCount() {
		return locales.count();
	}

	@Override
	public WorkLocale getLocale(Integer localeId) {
		Assert.notNull(localeId);

		return locales.findOneById(localeId);
	}

	@Override
	public List<WorkLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public List<WorkLocale> getLocalesByWork(Integer workId) {
		Assert.notNull(workId);

		return locales.findAllByWorkId(workId);
	}

	/*
	 * SETTERS
	 */

	@Override
	public Work setWork(Work work) {
		Assert.notNull(work);

		return works.save(work);
	}

	@Override
	public WorkLocale setLocale(WorkLocale locale) {
		Assert.notNull(locale);

		return locales.save(locale);
	}

	/*
	 * DELETERS
	 */

	@Override
	public void delWork(Integer workId) {
		Assert.notNull(workId);

		works.delete(workId);
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
	public Boolean hasWork(Integer workId) {
		Assert.notNull(workId);

		return works.exists(workId);
	}

	@Override
	public Boolean hasLocale(Integer workId, String language) {
		Assert.notNull(workId);
		Assert.notNull(language);

		return locales.findAllByWorkId(workId).stream().filter(o -> o.getLanguage().equals(language)).findFirst()
				.isPresent();
	}

	/*
	 * IMPORTERS
	 */

	@Override
	public Work importWork(Integer wikidataId) throws Exception {
		Assert.notNull(wikidataId);

		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

		Work work = new Work();
		work.setWikidataId(wikidataId);
		work.setSlug(item.getLabels().get("en").getText().replace(" ", "_"));

		if (item.hasStatement("P50")) {
			Integer id = Integer.parseInt(item.findStatementItemIdValue("P50").getId().replaceFirst("^Q", ""));
			Author author = (authors.getAuthorByWikidata(id) != null) ? authors.getAuthorByWikidata(id)
					: authors.importAuthor(id);

			work = works.save(work.setAuthor(author));
		} else {
			throw new NoSuchEntityErrorException("No Author found in Wikidata");
		}

		importLocales(work.getId());

		return work;
	}

	@Override
	public WorkLocale importLocale(Integer workId, String language) throws Exception {
		Assert.notNull(workId);
		Assert.notNull(language);
		Assert.isTrue(!hasLocale(workId, language));

		Work work = getWork(workId);
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + work.getWikidataId());
		Map<String, MonolingualTextValue> labels = item.getLabels();
		MonolingualTextValue label = labels.get(language);

		WorkLocale locale = new WorkLocale(work);
		locale.setLanguage(language);
		locale.setName(label.getText());
		locale.setHref("https://" + language + ".wikipedia.org/wiki/" + label.getText().replace(" ", "_"));

		return locales.save(locale);
	}

	@Override
	public List<WorkLocale> importLocales(Integer workId) {
		Assert.notNull(workId);

		for (String language : env.getProperty("tiwolij.localizations").split(", ")) {
			try {
				importLocale(workId, language);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return getLocalesByWork(workId);
	}

}
