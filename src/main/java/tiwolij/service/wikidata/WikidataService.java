package tiwolij.service.wikidata;

import java.net.URL;

import tiwolij.domain.Locale;

public interface WikidataService {

	public Locale extractLocale(Integer wikidataId, String language);

	public Integer extractWikidataId(URL url);

	public Integer extractAuthor(Integer wikidataId);

	public String extractSlug(Integer wikidataId);

	public URL extractImage(Integer wikidataId);

	public String extractImageAttribution(Integer wikidataId);

}
