package tiwolij.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/tiwolij/import")
public class Import {

	@Autowired
	private Environment env;

	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/import");

		mv.addObject("formats", env.getProperty("tiwolij.import.format", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.localizations", String[].class));
		return mv;
	}

	@PostMapping({ "", "/" })
	public ModelAndView root(@RequestParam("file") MultipartFile file, @RequestParam("encoding") String encoding,
			@RequestParam("format") String format, @RequestParam(name = "language", defaultValue = "") String language)
			throws Exception {

		ModelAndView mv = new ModelAndView("backend/report");
		Map<String, Exception> errors = new HashMap<String, Exception>();
		Map<String, QuoteLocale> imports = new HashMap<String, QuoteLocale>();

		List<String> languages = Arrays.asList(env.getProperty("tiwolij.localizations", String[].class));
		Pattern regexDate = Pattern.compile("(\\d+)[./-](\\d+)");
		Pattern regexLang = Pattern.compile("://(.{2})\\.wikipedia");
		Pattern regexWDId = Pattern.compile("Q(\\d+)");

		Quote quote;
		QuoteLocale quoteLocale;

		Work work;
		WorkLocale workLocale;

		Author author;
		AuthorLocale authorLocale;

		String line = "";
		Integer lineno = 1;
		Iterator<String> i1;
		Iterator<String> i2;
		Map<String, String> values = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(file.getBytes()), encoding));

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

				/*
				 * QUOTE
				 */

				quote = new Quote();
				quoteLocale = new QuoteLocale();

				// corpus
				if (!values.containsKey("corpus") || values.get("corpus").isEmpty())
					throw new ParseException("Missing corpus", lineno);

				quoteLocale.setCorpus(values.get("corpus"));

				// schedule
				if (!values.containsKey("schedule") || values.get("schedule").isEmpty())
					throw new ParseException("Missing schedule", lineno);

				Matcher schedule = regexDate.matcher(values.get("schedule"));

				if (!schedule.find())
					throw new ParseException("Missing schedule", lineno);

				String day = String.format("%02d", Integer.parseInt(schedule.group(1)));
				String month = String.format("%02d", Integer.parseInt(schedule.group(2)));
				quoteLocale.setSchedule(day + "-" + month);

				// language
				if (!language.isEmpty()) {
					if (!languages.contains(language))
						throw new ParseException("Missing language", lineno);

					quoteLocale.setLanguage(language);
				}

				else if (values.containsKey("language") && !values.get("language").isEmpty()) {
					if (!languages.contains(values.get("language")))
						throw new ParseException("Missing language", lineno);

					quoteLocale.setLanguage(values.get("language"));
				}

				// language(work_href)
				else if (values.containsKey("work_href") && !values.get("work_href").isEmpty()) {
					Matcher matchLang = regexLang.matcher(values.get("work_href"));

					if (!matchLang.find() || !languages.contains(matchLang.group(1)))
						throw new ParseException("Missing language", lineno);

					quoteLocale.setLanguage(matchLang.group(1));
				}

				// language(author_href)
				else if (values.containsKey("author_href") && !values.get("author_href").isEmpty()) {
					Matcher matchLang = regexLang.matcher(values.get("author_href"));

					if (!matchLang.find() || !languages.contains(matchLang.group(1)))
						throw new ParseException("Missing language", lineno);

					quoteLocale.setLanguage(matchLang.group(1));
				}

				// no language
				else
					throw new ParseException("Missing language", lineno);

				// source [meta]
				if (values.containsKey("source [meta]") && !values.get("source [meta]").isEmpty()) {
					if (values.get("source [meta]").contains(" ")) {
						String source = values.get("source [meta]");
						quoteLocale.setHref(source.substring(0, source.indexOf(" ")));
						quoteLocale.setMeta(source.substring(source.indexOf(" ") + 1).replaceAll("-", " "));
					} else
						quoteLocale.setHref(values.get("source [meta]"));
				}

				// no source
				else
					throw new ParseException("Missing source", lineno);

				// duplicate
				for (QuoteLocale l : quotes.getLocalesByScheduleAndLang(quoteLocale.getSchedule(),
						quoteLocale.getLanguage()))
					if (levenshteinDistance(l.getCorpus(), quoteLocale.getCorpus()) < 10)
						throw new DuplicateKeyException("Duplicate entry");

				/*
				 * WORK
				 */

				work = new Work();
				workLocale = null;

				// work_id
				if (values.containsKey("work_id") && !values.get("work_id").isEmpty()) {
					Matcher id = regexWDId.matcher(values.get("work_id"));

					if (!id.find())
						throw new ParseException("Missing work", lineno);

					Integer wikidataId = Integer.parseInt(id.group(1));
					work = works.hasWorkByWikidataId(wikidataId) ? works.getWorkByWikidataId(wikidataId)
							: works.importWorkByWikidataId(wikidataId);
				}

				// work_href
				else if (values.containsKey("work_href") && !values.get("work_href").isEmpty()) {
					work = works.importWorkByArticle(values.get("work_href"));
				}

				// work_slug, work_name
				else if ((values.containsKey("work_slug") && !values.get("work_slug").isEmpty())
						|| (values.containsKey("work_name") && !values.get("work_name").isEmpty())) {

					String slug = values.containsKey("work_slug") && !values.get("work_slug").isEmpty()
							? values.get("work_slug") : values.get("work_name").replace(" ", "_");

					work = works.hasWorkBySlug(slug) ? works.getWorkBySlug(slug) : new Work().setSlug(slug);
				}

				// no work
				else
					throw new ParseException("Missing work", lineno);

				/*
				 * AUTHOR
				 */

				author = new Author();
				authorLocale = null;

				// work_author
				if (work.getAuthor() != null) {
					author = work.getAuthor();
				}

				// author_id
				else if (values.containsKey("author_id") && !values.get("author_id").isEmpty()) {
					Matcher id = regexWDId.matcher(values.get("author_id"));

					if (!id.find())
						throw new ParseException("Missing author", lineno);

					Integer wikidataId = Integer.parseInt(id.group(1));
					author = authors.hasAuthorByWikidataId(wikidataId) ? authors.getAuthorByWikidataId(wikidataId)
							: authors.importAuthorByWikidataId(wikidataId);
				}

				// author_href
				else if (values.containsKey("author_href") && !values.get("author_href").isEmpty()) {
					author = authors.importAuthorByArticle(values.get("author_href"));
				}

				// author_slug, author_name
				else if ((values.containsKey("author_slug") && !values.get("author_slug").isEmpty())
						|| (values.containsKey("author_name") && !values.get("author_name").isEmpty())) {

					String slug = values.containsKey("author_slug") && !values.get("author_slug").isEmpty()
							? values.get("author_slug") : values.get("author_name").replace(" ", "_");

					author = authors.hasAuthorBySlug(slug) ? authors.getAuthorBySlug(slug) : new Author().setSlug(slug);
				}

				// no author
				else
					throw new ParseException("Missing author", lineno);

				/*
				 * SAVE
				 */

				// author
				if (author.getId() == null)
					authors.setAuthor(author);

				// authorLocale
				if (!authors.hasLocale(author.getId(), quoteLocale.getLanguage()))
					try {
						authors.importLocale(author.getId(), quoteLocale.getLanguage());
					} catch (Exception e) {
						if (values.containsKey("author_name") && !values.get("author_name").isEmpty()) {
							authorLocale = new AuthorLocale(author);
							authorLocale.setName(values.get("author_name"));
							authorLocale.setLanguage(quoteLocale.getLanguage());
							authors.setLocale(authorLocale);
						}
					}

				// work
				if (work.getId() == null)
					works.setWork(work.setAuthor(author));

				// workLocale
				if (!works.hasLocale(work.getId(), quoteLocale.getLanguage()))
					try {
						works.importLocale(work.getId(), quoteLocale.getLanguage());
					} catch (Exception e) {
						if (values.containsKey("work_name") && !values.get("work_name").isEmpty()) {
							workLocale = new WorkLocale(work);
							workLocale.setName(values.get("work_name"));
							workLocale.setLanguage(quoteLocale.getLanguage());
							works.setLocale(workLocale);
						}
					}

				// quote, quoteLocale
				quotes.setQuote(quote.setWork(work));
				quotes.setLocale(quoteLocale.setQuote(quote));

				imports.put(line, quoteLocale);
			} catch (Exception error) {
				errors.put(line, error);
			}
		}

		mv.addObject("format", format);
		mv.addObject("quotes", imports);
		mv.addObject("errors", errors);
		return mv;
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
