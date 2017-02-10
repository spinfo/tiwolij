package tiwolij.service.wikidata;

import java.net.URL;

import org.springframework.stereotype.Component;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;

@Component
public class WikidataServiceImpl implements WikidataService {

	private final WikidataRepository wikidata;

	public WikidataServiceImpl(WikidataRepository wikidata) {
		this.wikidata = wikidata;
	}

	@Override
	public Author getAuthor(Author author) {
		return wikidata.getAuthor(author);
	}

	@Override
	public Work getWork(Work work) {
		return wikidata.getWork(work);
	}

	@Override
	public Quote getQuote(Quote quote) {
		return wikidata.getQuote(quote);
	}

	@Override
	public Locale getLocale(Integer wikidataId, String language) {
		return wikidata.extractLocale(wikidataId, language);
	}

	@Override
	public Integer extractWikidataId(URL url) {
		return wikidata.extractWikidataId(url);
	}

	@Override
	public Integer extractAuthor(Integer wikidataId) {
		return wikidata.extractAuthor(wikidataId);
	}

	@Override
	public String extractSlug(Integer wikidataId) {
		return wikidata.extractSlug(wikidataId);
	}

	@Override
	public URL extractImage(Integer wikidataId) {
		return wikidata.extractImage(wikidataId);
	}

	@Override
	public String extractImageAttribution(Integer wikidataId) {
		return wikidata.extractImageAttribution(wikidataId);
	}

}
