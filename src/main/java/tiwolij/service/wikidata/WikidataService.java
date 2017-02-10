package tiwolij.service.wikidata;

import java.net.URL;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;

public interface WikidataService {

	public Author getAuthor(Author author);

	public Work getWork(Work work);

	public Quote getQuote(Quote quote);

	public Locale getLocale(Integer wikidataId, String language);

	public Integer extractWikidataId(URL url);

	public Integer extractAuthor(Integer wikidataId);

	public String extractSlug(Integer wikidataId);

	public URL extractImage(Integer wikidataId);

	public String extractImageAttribution(Integer wikidataId);

}
