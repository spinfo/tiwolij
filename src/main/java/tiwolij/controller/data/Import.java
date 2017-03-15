package tiwolij.controller.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.wikidata.wdtk.wikibaseapi.apierrors.NoSuchEntityErrorException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.tsv.TSVServiceImpl;
import tiwolij.service.wikidata.WikidataService;
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
	EntityManager entityManager;

	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	@Autowired
	private WikidataService wikidata;

	@Autowired
	private TSVServiceImpl tsv;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/data/import");

		if (session.getAttribute("progress") != null) {
			return new ModelAndView("redirect:/tiwolij/data/import/progress");
		}

		return mv;
	}

	@PostMapping("/author")
	public String author(@ModelAttribute Author author) throws Exception {
		if (!author.hasWikidataId()) {
			throw new NoSuchEntityErrorException("No Wikidata ID given");
		} else if (authors.existsByWikidataId(author.getWikidataId())) {
			throw new DuplicateKeyException("Duplicate Author");
		}

		return "redirect:/tiwolij/authors/view?authorId=" + authors.save(wikidata.getAuthor(author)).getId();
	}

	@PostMapping("/work")
	public String work(@ModelAttribute Work work) throws Exception {
		if (!work.hasWikidataId()) {
			throw new NoSuchEntityErrorException("No Wikidata ID given");
		} else if (works.existsByWikidataId(work.getWikidataId())) {
			throw new DuplicateKeyException("Duplicate Work");
		}

		Author author = new Author().setWikidataId(wikidata.extractAuthor(work.getWikidataId()));
		work.setAuthor((author.hasWikidataId()) ? authors.save(wikidata.getAuthor(author)) : work.getAuthor());

		return "redirect:/tiwolij/works/view?workId=" + works.save(wikidata.getWork(work)).getId();
	}

	@GetMapping("/locales")
	public String locales(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId,
			@RequestParam(name = "workId", defaultValue = "0") Integer workId) throws Exception {
		if (authorId > 0) {
			Author author = authors.getOneById(authorId);

			if (!author.hasWikidataId()) {
				throw new NoSuchEntityErrorException("No Wikidata ID given");
			}

			for (String i : env.getProperty("tiwolij.locales.allowed", String[].class)) {
				author.addLocale(wikidata.getLocale(author.getWikidataId(), i));
			}

			return "redirect:/tiwolij/authors/view?authorId=" + authors.save(author).getId();
		} else if (workId > 0) {
			Work work = works.getOneById(workId);

			if (!work.hasWikidataId()) {
				throw new NoSuchEntityErrorException("No Wikidata ID given");
			}

			for (String i : env.getProperty("tiwolij.locales.allowed", String[].class)) {
				work.addLocale(wikidata.getLocale(work.getWikidataId(), i));
			}

			return "redirect:/tiwolij/works/view?workId=" + works.save(work).getId();
		} else {
			throw new NoSuchEntityErrorException("No Wikidata ID given");
		}
	}

	@GetMapping("/json")
	public ModelAndView json() {
		ModelAndView mv = new ModelAndView("backend/data/import_json");

		if (session.getAttribute("progress") != null) {
			return new ModelAndView("redirect:/tiwolij/data/import/progress");
		}

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		return mv;
	}

	@PostMapping("/json")
	public String json(@RequestParam(name = "review", defaultValue = "false") Boolean review,
			@RequestParam(name = "wikidata", defaultValue = "false") Boolean wikifill,
			@RequestParam(name = "heideltag", defaultValue = "false") Boolean heideltag,
			@RequestParam(name = "levensthein", defaultValue = "false") Boolean levensthein,
			@RequestParam("file") MultipartFile file) throws Exception {

		if (session.getAttribute("progress") != null) {
			return "redirect:/tiwolij/data/import/progress";
		}

		AtomicInteger key = new AtomicInteger();
		ObjectMapper mapper = new ObjectMapper();

		session.setAttribute("progress", "json");
		List<Quote> list = mapper.readValue(file.getBytes(), new TypeReference<List<Quote>>() {
		});

		Map<Integer, Quote> results = list.stream()
				.collect(Collectors.toMap(i -> key.incrementAndGet(), i -> i.setId(null)));
		Map<Integer, Exception> errors = new HashMap<Integer, Exception>();

		if (wikifill) {
			wikifill(results, errors);
		}

		if (heideltag) {
			heideltag(results, errors);
		}

		if (levensthein) {
			levensthein(results, errors);
		}

		session.setAttribute("results", results);
		session.setAttribute("errors", errors);

		if (review) {
			session.setAttribute("progress", "review");
			return "redirect:/tiwolij/data/import/review";
		} else {
			session.setAttribute("progress", "process");
			return "redirect:/tiwolij/data/import/process";
		}
	}

	@GetMapping("/tsv")
	public ModelAndView tsv() {
		ModelAndView mv = new ModelAndView("backend/data/import_tsv");

		if (session.getAttribute("progress") != null) {
			return new ModelAndView("redirect:/tiwolij/data/import/progress");
		}

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		mv.addObject("formats", env.getProperty("tiwolij.import.formats", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@PostMapping("/tsv")
	public String tsv(@RequestParam(name = "review", defaultValue = "false") Boolean review,
			@RequestParam(name = "forcelang", defaultValue = "") String forcelang,
			@RequestParam(name = "wikidata", defaultValue = "false") Boolean wikifill,
			@RequestParam(name = "heideltag", defaultValue = "false") Boolean heideltag,
			@RequestParam(name = "levensthein", defaultValue = "false") Boolean levensthein,
			@RequestParam("format") String format, @RequestParam("encoding") String encoding,
			@RequestParam("file") MultipartFile file) throws Exception {

		if (session.getAttribute("progress") != null) {
			return "redirect:/tiwolij/data/import/progress";
		}

		session.setAttribute("progress", "tsv");
		tsv.process(encoding, Arrays.asList(format.split(";")), forcelang, file.getBytes());

		Map<Integer, Quote> results = tsv.getResults();
		Map<Integer, Exception> errors = tsv.getErrors();

		if (wikifill) {
			wikifill(results, errors);
		}

		if (heideltag) {
			heideltag(results, errors);
		}

		if (levensthein) {
			levensthein(results, errors);
		}

		session.setAttribute("results", results);
		session.setAttribute("errors", errors);

		if (review) {
			session.setAttribute("progress", "review");
			return "redirect:/tiwolij/data/import/review";
		} else {
			session.setAttribute("progress", "process");
			return "redirect:/tiwolij/data/import/process";
		}
	}

	@GetMapping("review")
	public ModelAndView review() {
		ModelAndView mv = new ModelAndView("backend/data/review");

		if ((String) session.getAttribute("progress") != "review") {
			if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
				return new ModelAndView("redirect:/tiwolij/data/import");
			} else {
				return new ModelAndView("redirect:/tiwolij/data/import/report");
			}
		}

		mv.addObject("results", session.getAttribute("results"));
		mv.addObject("errors", session.getAttribute("errors"));
		return mv;
	}

	@RequestMapping("process")
	public String process(@RequestParam(value = "lines[]", defaultValue = "") Set<Integer> lines) {
		if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
			return "redirect:/tiwolij/data/import";
		}

		Map<Integer, Quote> results = (HashMap<Integer, Quote>) session.getAttribute("results");
		Map<Integer, Exception> errors = (HashMap<Integer, Exception>) session.getAttribute("errors");

		lines = (lines.isEmpty()) ? results.keySet() : lines;
		results = (results == null) ? new HashMap<Integer, Quote>() : results;
		errors = (errors == null) ? new HashMap<Integer, Exception>() : errors;
		Integer last = lines.toArray(new Integer[lines.size()])[lines.size() - 1];

		for (Integer i : lines) {
			try {
				session.setAttribute("progress", "import:" + (new Float(i) / last * 100));

				Quote quote = results.get(i);
				Author author = quote.getAuthor();
				Work work = quote.getWork();

				List<Locale> authorLocales = (author.hasLocales()) ? author.getLocales() : new ArrayList<Locale>();
				List<Locale> workLocales = (work.hasLocales()) ? work.getLocales() : new ArrayList<Locale>();

				author.setLocales(null);
				work.setLocales(null);

				if (authors.existsByWikidataId(author.getWikidataId())) {
					author = authors.getOneByWikidataId(author.getWikidataId()).merge(author);
					for (Locale j : authorLocales) {
						if (!author.hasLocale(j.getLanguage())) {
							author.addLocale(wikidata.getLocale(author.getWikidataId(), j.getLanguage()));
						}
					}
				} else if (authors.existsBySlug(author.getSlug())) {
					author = authors.getOneBySlug(author.getSlug()).merge(author);
					for (Locale j : authorLocales) {
						if (!author.hasLocale(j.getLanguage())) {
							author.addLocale(j);
						}
					}
				} else {
					for (Locale j : authorLocales) {
						if (!author.hasLocale(j.getLanguage())) {
							author.addLocale(j);
						}
					}
				}

				if (works.existsByWikidataId(work.getWikidataId())) {
					work = works.getOneByWikidataId(work.getWikidataId()).merge(work);
					for (Locale j : workLocales) {
						if (!work.hasLocale(j.getLanguage())) {
							work.addLocale(wikidata.getLocale(work.getWikidataId(), j.getLanguage()));
						}
					}
				} else if (works.existsBySlug(work.getSlug())) {
					work = works.getOneBySlug(work.getSlug()).merge(work);
					for (Locale j : workLocales) {
						if (!work.hasLocale(j.getLanguage())) {
							work.addLocale(j);
						}
					}
				} else {
					for (Locale j : workLocales) {
						if (!work.hasLocale(j.getLanguage())) {
							work.addLocale(j);
						}
					}
				}

				entityManager.clear();
				authors.save(author);
				works.save(work.setAuthor(author));
				quotes.save(quote.setWork(work));
				results.replace(i, quote);
			} catch (Exception e) {
				results.remove(i);
				errors.put(i, e);
			}
		}

		session.setAttribute("results", results);
		session.setAttribute("errors", errors);
		session.removeAttribute("progress");

		return "redirect:/tiwolij/data/import/report";
	}

	@GetMapping("progress")
	public ModelAndView progress() throws Exception {
		ModelAndView mv = new ModelAndView("backend/data/progress");

		if (session.getAttribute("progress") == null) {
			if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
				return new ModelAndView("redirect:/tiwolij/data/import");
			} else {
				return new ModelAndView("redirect:/tiwolij/data/import/report");
			}
		} else if ((String) session.getAttribute("progress") == "review") {
			return new ModelAndView("redirect:/tiwolij/data/import/review");
		} else if ((String) session.getAttribute("progress") == "process") {
			return new ModelAndView("redirect:/tiwolij/data/import/process");
		}

		mv.addObject("progress", session.getAttribute("progress"));
		return mv;
	}

	@RequestMapping("report")
	public ModelAndView report() {
		ModelAndView mv = new ModelAndView("backend/data/report");

		if (session.getAttribute("progress") != null) {
			return new ModelAndView("redirect:/tiwolij/data/import/progress");
		}

		if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
			return new ModelAndView("redirect:/tiwolij/data/import");
		}

		Map<Integer, Quote> results = (HashMap<Integer, Quote>) session.getAttribute("results");
		Map<Integer, Exception> errors = (HashMap<Integer, Exception>) session.getAttribute("errors");

		mv.addObject("results", results);
		mv.addObject("errors", errors);
		return mv;
	}

	@GetMapping("clear")
	public String clear() {
		session.removeAttribute("results");
		session.removeAttribute("errors");
		session.removeAttribute("progress");

		return "redirect:/tiwolij/data/import";
	}

	private void wikifill(Map<Integer, Quote> results, Map<Integer, Exception> errors) {
		Iterator<Entry<Integer, Quote>> iter = results.entrySet().iterator();

		Integer count = 0;
		Integer total = results.values().size();
		session.setAttribute("progress", "wikidata:" + count);

		while (iter.hasNext()) {
			session.setAttribute("progress", "wikidata:" + (new Float(count++) / total * 100));
			Quote i = iter.next().getValue();

			wikidata.getQuote(i);
		}
	}

	private void heideltag(Map<Integer, Quote> results, Map<Integer, Exception> errors) {
		Heideltimer timer = new Heideltimer();
		Iterator<Entry<Integer, Quote>> iter = results.entrySet().iterator();

		Integer count = 0;
		Integer total = results.values().size();
		session.setAttribute("progress", "heideltag:" + count);

		while (iter.hasNext()) {
			session.setAttribute("progress", "heideltag:" + (new Float(count++) / total * 100));
			Quote i = iter.next().getValue();

			i.setYear((!i.hasYear()) ? timer.getYear(i) : i.getYear());
			i.setTime((!i.hasTime()) ? timer.getTime(i) : i.getTime());
		}
	}

	private void levensthein(Map<Integer, Quote> results, Map<Integer, Exception> errors) {
		List<Quote> existing = quotes.getAll();
		Iterator<Entry<Integer, Quote>> iter = results.entrySet().iterator();
		Integer distance = env.getProperty("tiwolij.import.levenshtein", Integer.class);

		Integer count = 0;
		Integer total = results.values().size();
		session.setAttribute("progress", "levensthein:" + count);

		while (iter.hasNext()) {
			session.setAttribute("progress", "levensthein:" + (new Float(count++) / total * 100));
			Entry<Integer, Quote> i = iter.next();

			existing.stream().filter(j -> i.getValue().getSchedule().equals(j.getSchedule())).forEach(k -> {
				if (StringUtils.getLevenshteinDistance(i.getValue().getCorpus(), k.getCorpus()) < distance) {
					errors.put(i.getKey(), new DuplicateKeyException("Duplicate quote"));
					iter.remove();
				}
			});
		}
	}

}
