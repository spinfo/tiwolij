package tiwolij.service.tsv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;
import tiwolij.util.RegularExpressions;

@Component
public class TSVServiceImpl implements TSVService {

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

	private void parse(String field, String value) {
		switch (field) {
		case "schedule":
			quote().setSchedule((!quote().hasSchedule()) ? regex.datetime(value) : quote().getSchedule());
			break;
		case "language":
			quote().setLanguage((!quote().hasLanguage()) ? value : quote().getLanguage());
			authorLocale().setLanguage((!authorLocale().hasLanguage()) ? value : authorLocale().getLanguage());
			workLocale().setLanguage((!workLocale().hasLanguage()) ? value : workLocale().getLanguage());
			break;
		case "corpus":
			quote().setCorpus((!quote().hasCorpus()) ? value : quote().getCorpus());
			break;
		case "source [meta]":
			if (value.indexOf(" ") == -1) {
				quote().setHref((!quote().hasHref()) ? value : quote().getHref());
			} else {
				quote().setHref((!quote().hasHref()) ? value.split(" ")[0] : quote().getHref());
				quote().setMeta((!quote().hasMeta()) ? value.split(" ")[1].replaceAll("-", " ") : quote().getMeta());
			}
			break;
		case "author_id":
			author().setWikidataId((!author().hasWikidataId()) ? regex.wikidataId(value) : author().getWikidataId());
			break;
		case "author_slug":
			author().setSlug((!author().hasSlug()) ? value : author().getSlug());
			break;
		case "author_name":
			authorLocale().setName((!authorLocale().hasName()) ? value : authorLocale().getName());
			author().setSlug((!author().hasSlug()) ? value : author().getSlug());
			break;
		case "author_href":
			authorLocale().setHref((!authorLocale().hasHref()) ? value : authorLocale().getHref());
			authorLocale().setLanguage(
					(!authorLocale().hasLanguage()) ? regex.wikipediaLang(value) : authorLocale().getLanguage());
			workLocale().setLanguage(
					(!workLocale().hasLanguage()) ? regex.wikipediaLang(value) : workLocale().getLanguage());
			quote().setLanguage((!quote().hasLanguage()) ? regex.wikipediaLang(value) : quote().getLanguage());
			break;
		case "work_id":
			work().setWikidataId((!work().hasWikidataId()) ? regex.wikidataId(value) : work().getWikidataId());
			break;
		case "work_slug":
			work().setSlug((!work().hasSlug()) ? value : work().getSlug());
			break;
		case "work_name":
			workLocale().setName((!workLocale().hasName()) ? value : workLocale().getName());
			work().setSlug((!work().hasSlug()) ? value : work().getSlug());
			break;
		case "work_href":
			workLocale().setHref((!workLocale().hasHref()) ? value : workLocale().getHref());
			authorLocale().setLanguage(
					(!authorLocale().hasLanguage()) ? regex.wikipediaLang(value) : authorLocale().getLanguage());
			workLocale().setLanguage(
					(!workLocale().hasLanguage()) ? regex.wikipediaLang(value) : workLocale().getLanguage());
			quote().setLanguage((!quote().hasLanguage()) ? regex.wikipediaLang(value) : quote().getLanguage());
			break;
		}
	}

	private void validate(Integer lineno) throws Exception {
		if (!author().hasWikidataId()) {
			if (!author().hasSlug()) {
				throw new ParseException("Missing author (no slug)", lineno);
			}
			if (!authorLocale().hasLanguage()) {
				throw new ParseException("Missing author locale (no language)", lineno);
			}
			if (!authorLocale().hasName()) {
				throw new ParseException("Missing author locale (no name)", lineno);
			}
		}

		if (!work().hasWikidataId()) {
			if (!work().hasSlug()) {
				throw new ParseException("Missing work (no slug)", lineno);
			}
			if (!workLocale().hasLanguage()) {
				throw new ParseException("Missing work locale (no language)", lineno);
			}
			if (!workLocale().hasName()) {
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
		return quote().getAuthor();
	}

	private Work work() {
		return quote().getWork();
	}

	private Locale authorLocale() {
		return author().getLocales().iterator().next();
	}

	private Locale workLocale() {
		return work().getLocales().iterator().next();
	}

	private Quote quote() {
		return currentQuote;
	}

}
