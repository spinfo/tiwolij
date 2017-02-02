package tiwolij.service.tsv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;
import tiwolij.service.wikidata.WikidataRepository;
import tiwolij.util.RegularExpressions;

@Component
public class TSVServiceImpl implements TSVService {

	@Autowired
	private WikidataRepository wikidata;

	private RegularExpressions regex = new RegularExpressions();

	private Map<Integer, Quote> results;

	private Map<Integer, Exception> errors;

	private Quote currentQuote;

	@Override
	public Map<Integer, Quote> getResults() {
		return results;
	}

	@Override
	public Map<Integer, Exception> getErrors() {
		return errors;
	}

	@Override
	public void process(String encoding, List<String> fields, String language, byte[] tsv) throws Exception {
		results = new LinkedHashMap<Integer, Quote>();
		errors = new LinkedHashMap<Integer, Exception>();

		String line;
		Integer lineno;

		String field;
		String value;
		Iterator<String> fieldIterator;
		Iterator<String> valueIterator;

		InputStream stream = new ByteArrayInputStream(tsv);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));

		lineno = 0;
		while ((line = reader.readLine()) != null) {
			lineno++;

			if (line.trim().isEmpty() || line.startsWith("#") || line.startsWith("//")) {
				continue;
			}

			currentQuote = new Quote(new Work(new Author().addLocale(new Locale())).addLocale(new Locale()));

			if (!language.isEmpty()) {
				authorLocale().setLanguage(language);
				workLocale().setLanguage(language);
				quote().setLanguage(language);
			}

			fieldIterator = fields.iterator();
			valueIterator = Arrays.asList(line.split("\t")).iterator();

			try {
				while (fieldIterator.hasNext() || valueIterator.hasNext()) {
					field = fieldIterator.next().trim();
					value = valueIterator.next().trim();

					if (value != null && !value.isEmpty()) {
						parse(field, value);
					}
				}

				validate(lineno);
				results.put(lineno, currentQuote);
			} catch (Exception error) {
				errors.put(lineno, error);
				error.printStackTrace();
			}
		}
	}

	private void parse(String field, String value) throws Exception {
		switch (field) {
		case "schedule":
			if (quote().getSchedule() == null) {
				quote().setSchedule(regex.datetime(value));
			}
			break;
		case "language":
			if (quote().getLanguage() == null) {
				quote().setLanguage(value);
			}
			if (authorLocale().getLanguage() == null) {
				authorLocale().setLanguage(value);
			}
			if (workLocale().getLanguage() == null) {
				workLocale().setLanguage(value);
			}
			break;
		case "corpus":
			if (quote().getCorpus() == null) {
				quote().setCorpus(value);
			}
			break;
		case "source [meta]":
			if (quote().getHref() == null) {
				if (value.indexOf(" ") == -1) {
					quote().setHref(value);
				} else {
					quote().setHref(value.split(" ")[0]);
					quote().setMeta(value.split(" ")[1].replaceAll("-", " "));
				}
			}
			break;
		case "author_id":
			if (author().getWikidataId() == null) {
				author().setWikidataId(regex.wikidataId(value));
			}
			break;
		case "author_slug":
			author().setSlug(value);
			break;
		case "author_name":
			if (authorLocale().getName() == null) {
				authorLocale().setName(value);
			}
			if (author().getSlug() == null) {
				author().setSlug(value);
			}
			break;
		case "author_href":
			if (authorLocale().getHref() == null) {
				authorLocale().setHref(value);
			}
			if (author().getWikidataId() == null) {
				author().setWikidataId(wikidata.extractWikidataId(new URL(value)));
			}
			if (authorLocale().getLanguage() == null) {
				authorLocale().setLanguage(regex.wikipediaLang(value));
			}
			if (workLocale().getLanguage() == null) {
				workLocale().setLanguage(regex.wikipediaLang(value));
			}
			if (quote().getLanguage() == null) {
				quote().setLanguage(regex.wikipediaLang(value));
			}
			break;
		case "work_id":
			if (work().getWikidataId() == null) {
				work().setWikidataId(regex.wikidataId(value));
			}
			if (author().getWikidataId() == null) {
				author().setWikidataId(wikidata.extractAuthor(work().getWikidataId()));
			}
			break;
		case "work_slug":
			work().setSlug(value);
			break;
		case "work_name":
			if (workLocale().getName() == null) {
				workLocale().setName(value);
			}
			if (work().getSlug() == null) {
				work().setSlug(value);
			}
			break;
		case "work_href":
			if (workLocale().getHref() == null) {
				workLocale().setHref(value);
			}
			if (work().getWikidataId() == null) {
				work().setWikidataId(wikidata.extractWikidataId(new URL(value)));
			}
			if (author().getWikidataId() == null) {
				author().setWikidataId(wikidata.extractAuthor(wikidata.extractWikidataId(new URL(value))));
			}
			if (authorLocale().getLanguage() == null) {
				authorLocale().setLanguage(regex.wikipediaLang(value));
			}
			if (workLocale().getLanguage() == null) {
				workLocale().setLanguage(regex.wikipediaLang(value));
			}
			if (quote().getLanguage() == null) {
				quote().setLanguage(regex.wikipediaLang(value));
			}
			break;
		}
	}

	private void validate(Integer lineno) throws Exception {
		if (author().getWikidataId() == null) {
			if (author().getSlug() == null) {
				throw new ParseException("Missing author (no slug)", lineno);
			}
			if (authorLocale().getLanguage() == null) {
				throw new ParseException("Missing author locale (no language)", lineno);
			}
			if (authorLocale().getName() == null) {
				throw new ParseException("Missing author locale (no name)", lineno);
			}
		}

		if (work().getWikidataId() == null) {
			if (work().getSlug() == null) {
				throw new ParseException("Missing work (no slug)", lineno);
			}
			if (workLocale().getLanguage() == null) {
				throw new ParseException("Missing work locale (no language)", lineno);
			}
			if (workLocale().getName() == null) {
				throw new ParseException("Missing work locale (no name)", lineno);
			}
		}

		if (quote().getCorpus() == null) {
			throw new ParseException("Missing quote (no corpus)", lineno);
		}

		if (quote().getLanguage() == null) {
			throw new ParseException("Missing quote (no language)", lineno);
		}

		if (quote().getSchedule() == null) {
			throw new ParseException("Missing quote (no schedule)", lineno);
		}
	}

	private Author author() {
		return work().getAuthor();
	}

	private Locale authorLocale() {
		return author().getMappedLocales().entrySet().iterator().next().getValue();
	}

	private Work work() {
		return quote().getWork();
	}

	private Locale workLocale() {
		return work().getMappedLocales().entrySet().iterator().next().getValue();
	}

	private Quote quote() {
		return currentQuote;
	}

}
