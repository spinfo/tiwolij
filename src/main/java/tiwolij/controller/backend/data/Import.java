package tiwolij.controller.backend.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;
import tiwolij.util.Heideltimer;

@Controller
@RequestMapping("/tiwolij/data/import")
public class Import {

	@Autowired
	private Environment env;

	@Autowired
	private HttpSession session;

	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	List<String> languages;
	Heideltimer heideltimer;
	Pattern regexDate = Pattern.compile("(\\d+)[./-](\\d+)([./-](\\d{4}))?[^ ]*( \\d{2}:\\d{2}(:\\d{2})?)?");
	Pattern regexLang = Pattern.compile("://(.{2})\\.wikipedia");
	Pattern regexWDId = Pattern.compile("Q(\\d+)");
	Pattern regexName = Pattern.compile("wiki/([^/]*)$");

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/data/import");

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		mv.addObject("formats", env.getProperty("tiwolij.import.format", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.localizations", String[].class));
		return mv;
	}

	@PostMapping({ "", "/" })
	public ModelAndView root(@RequestParam("format") String format,
			@RequestParam(value = "lines[]") List<Integer> lines) throws Exception {
		ModelAndView mv = new ModelAndView("backend/data/report");

		Map<String, QuoteLocale> imports = (LinkedHashMap<String, QuoteLocale>) session.getAttribute("imports");
		imports = imports.entrySet().stream().filter(e -> lines.contains(e.getValue().getId()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		session.removeAttribute("imports");

		for (QuoteLocale locale : imports.values())
			save(locale);

		mv.addObject("format", format);
		mv.addObject("quotes", imports);
		return mv;
	}

	@PostMapping("/preview")
	public ModelAndView preview(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "forcelang", defaultValue = "") String forcelang,
			@RequestParam("format") String format, @RequestParam("encoding") String encoding,
			@RequestParam(name = "review", defaultValue = "false") Boolean review,
			@RequestParam(name = "heideltag", defaultValue = "false") Boolean heideltag) throws Exception {

		heideltimer = new Heideltimer();
		languages = Arrays.asList(env.getProperty("tiwolij.localizations", String[].class));

		ModelAndView mv = review ? new ModelAndView("backend/data/preview") : new ModelAndView("backend/data/report");
		Map<String, Exception> errors = new LinkedHashMap<String, Exception>();
		Map<String, QuoteLocale> imports = new LinkedHashMap<String, QuoteLocale>();

		if (!forcelang.isEmpty() && !languages.contains(forcelang))
			throw new ParseException("Missing language", 0);

		Quote quote;
		QuoteLocale locale;

		String line = "";
		Integer lineno = 1;
		Iterator<String> i1;
		Iterator<String> i2;
		Map<String, String> values = new HashMap<String, String>();
		InputStream stream = new ByteArrayInputStream(file.getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, encoding));

		while ((line = reader.readLine()) != null) {
			if (line.trim().isEmpty() || line.startsWith("#") || line.startsWith("//"))
				continue;

			try {
				lineno++;
				values.clear();

				i1 = Arrays.asList(format.split(";")).iterator();
				i2 = Arrays.asList(line.split("\t")).iterator();

				while (i1.hasNext() || i2.hasNext())
					values.put(i1.next().trim(), i2.next().trim());

				quote = new Quote();
				locale = new QuoteLocale(quote).setId(lineno);

				locale = setCorpus(locale, lineno, values);
				locale = setSchedule(locale, lineno, values);
				locale = !forcelang.isEmpty() ? locale.setLanguage(forcelang) : setLanguage(locale, lineno, values);
				locale = setSource(locale, lineno, values);
				locale = heideltag ? setHeideltime(locale) : locale;
				locale = setWork(locale, lineno, values);
				locale = setAuthor(locale, lineno, values);

				for (QuoteLocale l : quotes.getLocalesByScheduleAndLang(locale.getSchedule(), locale.getLanguage()))
					if (levenshteinDistance(l.getCorpus(), locale.getCorpus()) < 10)
						throw new DuplicateKeyException("Duplicate entry");

				imports.put(line, locale);
			} catch (Exception error) {
				errors.put(line, error);
				error.printStackTrace();
			}
		}

		if (review) {
			session.setAttribute("imports", imports);
		} else {
			for (QuoteLocale imported : imports.values())
				save(imported);
		}

		mv.addObject("format", format);
		mv.addObject("quotes", imports);
		mv.addObject("errors", errors);
		return mv;
	}

	private QuoteLocale setCorpus(QuoteLocale locale, Integer lineno, Map<String, String> values) throws Exception {
		if (!values.containsKey("corpus") || values.get("corpus").isEmpty())
			throw new ParseException("Missing corpus", lineno);

		locale.setCorpus(values.get("corpus"));

		return locale;
	}

	private QuoteLocale setSchedule(QuoteLocale locale, Integer lineno, Map<String, String> values) throws Exception {
		if (!values.containsKey("schedule") || values.get("schedule").isEmpty())
			throw new ParseException("Missing schedule", lineno);

		Matcher schedule = regexDate.matcher(values.get("schedule"));

		if (!schedule.find())
			throw new ParseException("Missing schedule", lineno);

		String day = String.format("%02d", Integer.parseInt(schedule.group(1)));
		String month = String.format("%02d", Integer.parseInt(schedule.group(2)));
		locale.setSchedule(day + "-" + month);

		if (schedule.group(3) != null && !schedule.group(4).isEmpty())
			locale.setYear(schedule.group(4));

		if (schedule.group(5) != null && !schedule.group(5).trim().isEmpty()) {
			String time = schedule.group(5);
			String secs = schedule.group(6) == null ? ":00" : "";
			locale.setTime(time + secs);
		}

		return locale;
	}

	private QuoteLocale setLanguage(QuoteLocale locale, Integer lineno, Map<String, String> values) throws Exception {
		if (values.containsKey("language") && !values.get("language").isEmpty()) {
			if (!languages.contains(values.get("language")))
				throw new ParseException("Missing language", lineno);

			locale.setLanguage(values.get("language"));
		}

		else if (values.containsKey("work_href") && !values.get("work_href").isEmpty()) {
			Matcher matchLang = regexLang.matcher(values.get("work_href"));

			if (!matchLang.find() || !languages.contains(matchLang.group(1)))
				throw new ParseException("Missing language", lineno);

			locale.setLanguage(matchLang.group(1));
		}

		else if (values.containsKey("author_href") && !values.get("author_href").isEmpty()) {
			Matcher matchLang = regexLang.matcher(values.get("author_href"));

			if (!matchLang.find() || !languages.contains(matchLang.group(1)))
				throw new ParseException("Missing language", lineno);

			locale.setLanguage(matchLang.group(1));
		}

		else
			throw new ParseException("Missing language", lineno);

		return locale;
	}

	private QuoteLocale setSource(QuoteLocale locale, Integer lineno, Map<String, String> values) throws Exception {
		if (values.containsKey("source [meta]") && !values.get("source [meta]").isEmpty()) {
			if (values.get("source [meta]").contains(" ")) {
				String source = values.get("source [meta]");
				locale.setHref(source.substring(0, source.indexOf(" ")));
				locale.setMeta(source.substring(source.indexOf(" ") + 1).replaceAll("-", " "));
			} else
				locale.setHref(values.get("source [meta]"));
		}

		else
			throw new ParseException("Missing source", lineno);

		return locale;
	}

	private QuoteLocale setHeideltime(QuoteLocale locale) throws Exception {
		locale.setYear(heideltimer.getYear(locale));
		locale.setTime(heideltimer.getTime(locale));

		return locale;
	}

	private QuoteLocale setWork(QuoteLocale locale, Integer lineno, Map<String, String> values) throws Exception {
		Work work = null;

		if (work == null)
			if (values.containsKey("work_id") && !values.get("work_id").isEmpty()) {
				Matcher id = regexWDId.matcher(values.get("work_id"));
				Integer wikidataId = null;

				if (id.find())
					wikidataId = Integer.parseInt(id.group(1));

				if (wikidataId > 0)
					work = works.hasWorkByWikidataId(wikidataId) ? works.getWorkByWikidataId(wikidataId)
							: new Work().setWikidataId(wikidataId);
			}

		if (work == null)
			if (values.containsKey("work_href") && !values.get("work_href").isEmpty()) {
				Integer wikidataId = getWDIdFromArticle(values.get("work_href"));

				if (wikidataId > 0)
					work = works.hasWorkByWikidataId(wikidataId) ? works.getWorkByWikidataId(wikidataId)
							: new Work().setWikidataId(wikidataId);
			}

		if (work == null)
			if (values.containsKey("work_slug") && !values.get("work_slug").isEmpty()) {
				String slug = values.get("work_slug");
				work = works.hasWorkBySlug(slug) ? works.getWorkBySlug(slug) : new Work().setSlug(slug);
			}

		if (work == null)
			if (values.containsKey("work_name") && !values.get("work_name").isEmpty()) {
				String slug = values.get("work_name").replace(" ", "_");
				work = works.hasWorkBySlug(slug) ? works.getWorkBySlug(slug) : new Work().setSlug(slug);
			}

		if (work == null)
			throw new ParseException("Missing work", lineno);

		if (values.containsKey("work_name") && !values.get("work_name").isEmpty()) {
			List<WorkLocale> locales = new ArrayList<WorkLocale>();
			WorkLocale workLocale = new WorkLocale(work);

			workLocale.setName(values.get("work_name"));
			workLocale.setLanguage(locale.getLanguage());

			locales.add(workLocale);
			work.setLocales(locales);
		}

		locale.getQuote().setWork(work);

		return locale;
	}

	private QuoteLocale setAuthor(QuoteLocale locale, Integer lineno, Map<String, String> values) throws Exception {
		Author author = locale.getQuote().getWork().getAuthor();

		if (author == null)
			if (values.containsKey("author_id") && !values.get("author_id").isEmpty()) {
				Matcher id = regexWDId.matcher(values.get("author_id"));
				Integer wikidataId = null;

				if (id.find())
					wikidataId = Integer.parseInt(id.group(1));

				if (wikidataId > 0)
					author = authors.hasAuthorByWikidataId(wikidataId) ? authors.getAuthorByWikidataId(wikidataId)
							: new Author().setWikidataId(wikidataId);
			}

		if (author == null)
			if (values.containsKey("author_href") && !values.get("author_href").isEmpty()) {
				Integer wikidataId = getWDIdFromArticle(values.get("author_href"));

				if (wikidataId > 0)
					author = authors.hasAuthorByWikidataId(wikidataId) ? authors.getAuthorByWikidataId(wikidataId)
							: new Author().setWikidataId(wikidataId);
			}

		if (author == null)
			if (values.containsKey("author_slug") && !values.get("author_slug").isEmpty()) {
				String slug = values.get("author_slug");
				author = authors.hasAuthorBySlug(slug) ? authors.getAuthorBySlug(slug) : new Author().setSlug(slug);
			}

		if (author == null)
			if (values.containsKey("author_name") && !values.get("author_name").isEmpty()) {
				String slug = values.get("author_name").replace(" ", "_");
				author = authors.hasAuthorBySlug(slug) ? authors.getAuthorBySlug(slug) : new Author().setSlug(slug);
			}

		if (author == null)
			if (locale.getQuote().getWork().getWikidataId() != null) {
				Integer wikidataId = getAuthorWDIdFromWorkWDId(locale.getQuote().getWork().getWikidataId());

				if (wikidataId > 0)
					author = authors.hasAuthorByWikidataId(wikidataId) ? authors.getAuthorByWikidataId(wikidataId)
							: new Author().setWikidataId(wikidataId);
			}

		if (author == null)
			if (values.containsKey("work_id") && !values.get("work_id").isEmpty()) {
				Matcher id = regexWDId.matcher(values.get("work_id"));
				Integer wikidataId = null;

				if (id.find())
					wikidataId = getAuthorWDIdFromWorkWDId(Integer.parseInt(id.group(1)));

				if (wikidataId > 0)
					author = authors.hasAuthorByWikidataId(wikidataId) ? authors.getAuthorByWikidataId(wikidataId)
							: new Author().setWikidataId(wikidataId);
			}

		if (author == null)
			if (values.containsKey("work_href") && !values.get("work_href").isEmpty()) {
				Integer workId = getWDIdFromArticle(values.get("work_href"));
				Integer authorId = getAuthorWDIdFromWorkWDId(workId);

				if (authorId > 0)
					author = authors.hasAuthorByWikidataId(authorId) ? authors.getAuthorByWikidataId(authorId)
							: new Author().setWikidataId(authorId);
			}

		if (author == null)
			throw new ParseException("Missing author", lineno);

		if (values.containsKey("author_name") && !values.get("author_name").isEmpty()) {
			List<AuthorLocale> locales = new ArrayList<AuthorLocale>();
			AuthorLocale authorLocale = new AuthorLocale(author);

			authorLocale.setName(values.get("author_name"));
			authorLocale.setLanguage(locale.getLanguage());

			locales.add(authorLocale);
			author.setLocales(locales);
		}

		locale.getQuote().getWork().setAuthor(author);

		return locale;
	}

	private void save(QuoteLocale quoteLocale) throws Exception {
		quoteLocale.setId(null);
		Quote quote = quoteLocale.getQuote();
		Work work = quote.getWork();
		Author author = work.getAuthor();

		List<WorkLocale> workLocales = null;
		List<AuthorLocale> authorLocales = null;

		if (work.getLocales() != null && work.get("locales") instanceof Map && work.getLocales().size() > 0)
			workLocales = new ArrayList<WorkLocale>(work.getLocales().values());

		if (author.getLocales() != null && author.get("locales") instanceof Map && author.getLocales().size() > 0)
			authorLocales = new ArrayList<AuthorLocale>(author.getLocales().values());

		// AUTHOR

		if (author.getId() == null || !authors.hasAuthor(author.getId())) {
			if (author.getWikidataId() != null)
				author = authors.hasAuthorByWikidataId(author.getWikidataId())
						? authors.getAuthorByWikidataId(author.getWikidataId())
						: authors.setAuthor(authors.importAuthorByWikidataId(author.getWikidataId()));

			else if (authors.hasAuthorBySlug(author.getSlug()))
				author = authors.getAuthorBySlug(author.getSlug());
			else
				author = authors.setAuthor(author);
		}

		// WORK

		if (work.getId() == null || !works.hasWork(work.getId())) {
			if (work.getWikidataId() != null)
				work = works.hasWorkByWikidataId(work.getWikidataId())
						? works.getWorkByWikidataId(work.getWikidataId()).setAuthor(author)
						: works.setWork(works.importWorkByWikidataId(work.getWikidataId()).setAuthor(author));

			else if (work.getSlug() != null && works.hasWorkBySlug(work.getSlug()))
				work = works.getWorkBySlug(work.getSlug());
			else
				work = works.setWork(work.setAuthor(author));
		}

		// LOCALES

		if (!authors.hasLocale(author.getId(), quoteLocale.getLanguage())) {
			AuthorLocale authorLocale = null;

			try {
				authorLocale = authors.importLocale(author.getId(), quoteLocale.getLanguage());
			} catch (Exception e) {
			}

			if (authorLocale == null && authorLocales != null)
				for (AuthorLocale locale : authorLocales)
					authors.setLocale(locale.setAuthor(author));
		}

		if (!works.hasLocale(work.getId(), quoteLocale.getLanguage())) {
			WorkLocale workLocale = null;

			try {
				workLocale = works.importLocale(work.getId(), quoteLocale.getLanguage());
			} catch (Exception e) {
			}

			if (workLocale == null && workLocales != null)
				for (WorkLocale locale : workLocales)
					works.setLocale(locale.setWork(work));
		}

		// QUOTE

		quotes.setQuote(quote.setWork(work));
		quotes.setLocale(quoteLocale.setQuote(quote));
	}

	private Integer getWDIdFromArticle(String article) throws Exception {
		Matcher wikiTitle = regexName.matcher(article);
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

		return wikidataId;
	}

	private Integer getAuthorWDIdFromWorkWDId(Integer wikidataId) throws Exception {
		Integer result = null;
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

		if (item.hasStatement("P50")) {
			String p50 = item.findStatementGroup("P50").getStatements().get(0).getValue().toString();
			Matcher wikiData = regexWDId.matcher(p50);

			if (wikiData.find())
				result = Integer.parseInt(wikiData.group(1));
		}

		return result;
	}

	// https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java
	private int levenshteinDistance(CharSequence lhs, CharSequence rhs) {
		int len0 = lhs.length() + 1;
		int len1 = rhs.length() + 1;

		int[] cost = new int[len0];
		int[] newcost = new int[len0];

		for (int i = 0; i < len0; i++)
			cost[i] = i;

		for (int j = 1; j < len1; j++) {
			newcost[0] = j;

			for (int i = 1; i < len0; i++) {
				int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;

				int cost_replace = cost[i - 1] + match;
				int cost_insert = cost[i] + 1;
				int cost_delete = newcost[i - 1] + 1;

				newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
			}

			int[] swap = cost;
			cost = newcost;
			newcost = swap;
		}

		return cost[len0 - 1];
	}

}
