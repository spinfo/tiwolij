package tiwolij.service.work;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.wikidata.WikidataService;

@Component
public class WorkWikidataSource {

	@Autowired
	private WikidataService wikidata;

	public Work byUrl(URL url) {
		Work work = null;
		Integer wikidataId = wikidata.getWikidataId(url);

		if (wikidataId != null) {
			work = byWikidataId(wikidataId);
		}

		return work;
	}

	public Work byWikidataId(Integer wikidataId) {
		Work work = new Work();

		work.setWikidataId(wikidataId);
		work.setSlug(wikidata.getSlug(wikidataId));

		return work;
	}

	public WorkLocale locale(Integer wikidataId, String language) {
		return wikidata.getWorkLocale(wikidataId, language);
	}

}
