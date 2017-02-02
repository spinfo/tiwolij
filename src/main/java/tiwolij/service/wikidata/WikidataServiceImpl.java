package tiwolij.service.wikidata;

import java.net.URL;

import org.springframework.stereotype.Component;

import tiwolij.domain.Locale;

@Component
public class WikidataServiceImpl implements WikidataService {

	private final WikidataRepository wikidata;

	public WikidataServiceImpl(WikidataRepository wikidata) {
		this.wikidata = wikidata;
	}

	@Override
	public Locale extractLocale(Integer wikidataId, String language) {
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
