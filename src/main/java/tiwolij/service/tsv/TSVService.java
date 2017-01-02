package tiwolij.service.tsv;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.wikidata.WikidataService;
import tiwolij.util.RegularExpressions;

@Component
public class TSVService {

	@Autowired
	private WikidataService wikidata;

	private RegularExpressions regex = new RegularExpressions();

	private Map<Integer, Author> authors;
	private Map<Integer, AuthorLocale> authorLocales;
	private Map<Integer, Work> works;
	private Map<Integer, WorkLocale> workLocales;
	private Map<Integer, Quote> quotes;
	private Map<Integer, QuoteLocale> quoteLocales;
	private Map<Integer, Exception> errors;

	private String line;
	private Integer lineno;

	public Map<Integer, QuoteLocale> getResults() {
		Map<Integer, QuoteLocale> result = new LinkedHashMap<Integer, QuoteLocale>();

		Integer linenno;
		Author author;
		AuthorLocale authorLocale;
		Work work;
		WorkLocale workLocale;
		Quote quote;
		QuoteLocale quoteLocale;

		for (Entry<Integer, QuoteLocale> locale : quoteLocales.entrySet()) {
			linenno = locale.getKey();
			author = authors.get(linenno);
			authorLocale = authorLocales.get(linenno);
			work = works.get(linenno);
			workLocale = workLocales.get(linenno);
			quote = quotes.get(linenno);
			quoteLocale = locale.getValue();

			quote.setWork(work);
			quote.setLocales(Arrays.asList(quoteLocale));
			quoteLocale.setQuote(quote);

			work.setAuthor(author);
			work.setLocales(Arrays.asList(workLocale));
			workLocale.setWork(work);

			author.setLocales(Arrays.asList(authorLocale));
			authorLocale.setAuthor(author);

			result.put(linenno, quoteLocale);
		}

		return result;
	}

	public Map<Integer, Exception> getErrors() {
		return errors;
	}

	public void process(String encoding, List<String> fields, String language, byte[] tsv) throws Exception {
		authors = new HashMap<Integer, Author>();
		authorLocales = new HashMap<Integer, AuthorLocale>();
		works = new HashMap<Integer, Work>();
		workLocales = new HashMap<Integer, WorkLocale>();
		quotes = new HashMap<Integer, Quote>();
		quoteLocales = new HashMap<Integer, QuoteLocale>();
		errors = new HashMap<Integer, Exception>();

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

			fieldIterator = fields.iterator();
			valueIterator = Arrays.asList(line.split("\t")).iterator();

			authors.put(lineno, new Author());
			authorLocales.put(lineno, new AuthorLocale());
			works.put(lineno, new Work());
			workLocales.put(lineno, new WorkLocale());
			quotes.put(lineno, new Quote());
			quoteLocales.put(lineno, new QuoteLocale());

			if (!language.isEmpty()) {
				authorLocale().setLanguage(language);
				workLocale().setLanguage(language);
				quoteLocale().setLanguage(language);
			}

			try {
				while (fieldIterator.hasNext() || valueIterator.hasNext()) {
					field = fieldIterator.next().trim();
					value = valueIterator.next().trim();

					if (value != null && !value.isEmpty()) {
						parse(field, value);
					}
				}

				validate();
			} catch (Exception error) {
				authors.remove(lineno);
				authorLocales.remove(lineno);
				works.remove(lineno);
				workLocales.remove(lineno);
				quotes.remove(lineno);
				quoteLocales.remove(lineno);

				errors.put(lineno, error);
			}

		}

	}

	private void parse(String field, String value) throws Exception {
		switch (field) {
		case "schedule":
			if (quoteLocale().getSchedule() == null || quoteLocale().getSchedule().isEmpty())
				quoteLocale().setSchedule(regex.getDatetime(value));
			break;
		case "language":
			if (authorLocale().getLanguage() == null || authorLocale().getLanguage().isEmpty())
				authorLocale().setLanguage(value);
			if (workLocale().getLanguage() == null || workLocale().getLanguage().isEmpty())
				workLocale().setLanguage(value);
			if (quoteLocale().getLanguage() == null || quoteLocale().getLanguage().isEmpty())
				quoteLocale().setLanguage(value);
			break;
		case "corpus":
			if (quoteLocale().getCorpus() == null || quoteLocale().getCorpus().isEmpty())
				quoteLocale().setCorpus(value);
			break;
		case "author_id":
			if (author().getWikidataId() == null || author().getWikidataId() <= 0)
				author().setWikidataId(regex.getWikidataId(value));
			break;
		case "work_id":
			if (author().getWikidataId() == null || author().getWikidataId() <= 0)
				author().setWikidataId(wikidata.getAuthor(regex.getWikidataId(value)));
			if (work().getWikidataId() == null || work().getWikidataId() <= 0)
				work().setWikidataId(regex.getWikidataId(value));
			break;
		case "author_name":
			if (authorLocale().getName() == null || authorLocale().getName().isEmpty())
				authorLocale().setName(value);

			if (author().getSlug() == null || author().getSlug().isEmpty())
				author().setSlug(value.replace(" ", "_"));
			break;
		case "work_name":
			if (workLocale().getName() == null || workLocale().getName().isEmpty())
				workLocale().setName(value);
			if (work().getSlug() == null || work().getSlug().isEmpty())
				work().setSlug(value.replace(" ", "_"));
			break;
		case "author_href":
			if (authorLocale().getHref() == null || authorLocale().getHref().isEmpty())
				authorLocale().setHref(value);
			if (author().getWikidataId() == null || author().getWikidataId() <= 0)
				author().setWikidataId(wikidata.getWikidataId(new URL(value)));

			if (authorLocale().getLanguage() == null || authorLocale().getLanguage().isEmpty())
				authorLocale().setLanguage(regex.getLanguageFromWikipedia(value));
			if (workLocale().getLanguage() == null || workLocale().getLanguage().isEmpty())
				workLocale().setLanguage(regex.getLanguageFromWikipedia(value));
			if (quoteLocale().getLanguage() == null || quoteLocale().getLanguage().isEmpty())
				quoteLocale().setLanguage(regex.getLanguageFromWikipedia(value));
			break;
		case "work_href":
			if (workLocale().getHref() == null || workLocale().getHref().isEmpty())
				workLocale().setHref(value);
			if (work().getWikidataId() == null || work().getWikidataId() <= 0)
				work().setWikidataId(wikidata.getWikidataId(new URL(value)));
			if (author().getWikidataId() == null || author().getWikidataId() <= 0)
				author().setWikidataId(wikidata.getAuthor(wikidata.getWikidataId(new URL(value))));

			if (authorLocale().getLanguage() == null || authorLocale().getLanguage().isEmpty())
				authorLocale().setLanguage(regex.getLanguageFromWikipedia(value));
			if (workLocale().getLanguage() == null || workLocale().getLanguage().isEmpty())
				workLocale().setLanguage(regex.getLanguageFromWikipedia(value));
			if (quoteLocale().getLanguage() == null || quoteLocale().getLanguage().isEmpty())
				quoteLocale().setLanguage(regex.getLanguageFromWikipedia(value));
			break;
		case "author_slug":
			if (author().getSlug() == null || author().getSlug().isEmpty())
				author().setSlug(value.replace(" ", "_"));
			break;
		case "work_slug":
			if (work().getSlug() == null || work().getSlug().isEmpty())
				work().setSlug(value.replace(" ", "_"));
			break;
		case "source [meta]":
			if (value.indexOf(" ") == -1) {
				quoteLocale().setHref(value);
			} else {
				quoteLocale().setHref(value.split(" ")[0]);
				quoteLocale().setMeta(value.split(" ")[1].replaceAll("-", " "));
			}
			break;
		}
	}

	private void validate() throws Exception {
		if (author().getWikidataId() == null) {
			if (author().getSlug() == null) {
				throw new ParseException("Missing author", lineno);
			}

			if (authorLocale().getLanguage() == null || authorLocale().getName() == null) {
				throw new ParseException("Missing author locale", lineno);
			}
		}

		if (work().getWikidataId() == null) {
			if (work().getSlug() == null) {
				throw new ParseException("Missing work", lineno);
			}

			if (workLocale().getLanguage() == null || workLocale().getName() == null) {
				throw new ParseException("Missing work locale", lineno);
			}
		}

		if (quoteLocale().getCorpus() == null) {
			throw new ParseException("Missing corpus", lineno);
		}

		if (quoteLocale().getLanguage() == null) {
			throw new ParseException("Missing Language", lineno);
		}

		if (quoteLocale().getSchedule() == null) {
			throw new ParseException("Missing schedule", lineno);
		}
	}

	private Author author() {
		return authors.get(lineno);
	}

	private AuthorLocale authorLocale() {
		return authorLocales.get(lineno);
	}

	private Work work() {
		return works.get(lineno);
	}

	private WorkLocale workLocale() {
		return workLocales.get(lineno);
	}

	private QuoteLocale quoteLocale() {
		return quoteLocales.get(lineno);
	}

}
