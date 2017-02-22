package tiwolij.controller.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.ResponseBody;
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
			return "redirect:/tiwolij/";
		}
	}

	@GetMapping("/json")
	public ModelAndView json() {
		ModelAndView mv = new ModelAndView("backend/data/import_json");

		if (session.getAttribute("progress") != null) {
			throw new IllegalStateException("Import already running");
		}

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		return mv;
	}

	@PostMapping("/json")
	public String json(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "review", defaultValue = "false") Boolean review) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		List<Quote> list = mapper.readValue(file.getBytes(), new TypeReference<List<Quote>>() {
		});

		list.stream().forEach(i -> i.setId(null).getWork().setId(null).getAuthor().setId(null));

		AtomicInteger key = new AtomicInteger();
		Map<Integer, Quote> results = list.stream().collect(Collectors.toMap(i -> key.incrementAndGet(), i -> i));
		session.setAttribute("results", results);

		return (review) ? "redirect:/tiwolij/data/import/review" : "redirect:/tiwolij/data/import/report";
	}

	@GetMapping("/tsv")
	public ModelAndView tsv() {
		ModelAndView mv = new ModelAndView("backend/data/import_tsv");

		if (session.getAttribute("progress") != null) {
			throw new IllegalStateException("Import already running");
		}

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		mv.addObject("formats", env.getProperty("tiwolij.import.formats", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@PostMapping("/tsv")
	public String tsv(@RequestParam("file") MultipartFile file, @RequestParam("format") String format,
			@RequestParam("encoding") String encoding,
			@RequestParam(name = "wikidata", defaultValue = "false") Boolean wikifill,
			@RequestParam(name = "heideltag", defaultValue = "false") Boolean heideltag,
			@RequestParam(name = "levensthein", defaultValue = "false") Boolean levensthein,
			@RequestParam(name = "review", defaultValue = "false") Boolean review,
			@RequestParam(name = "forcelang", defaultValue = "") String forcelang) throws Exception {

		session.setAttribute("progress", "tsv");
		tsv.process(encoding, Arrays.asList(format.split(";")), forcelang, file.getBytes());

		Map<Integer, Quote> results = tsv.getResults();
		Map<Integer, Exception> errors = tsv.getErrors();

		if (wikifill) {
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

		if (heideltag) {
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

		if (levensthein) {
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

		session.removeAttribute("progress");
		session.setAttribute("results", results);
		session.setAttribute("errors", errors);

		return (review) ? "redirect:/tiwolij/data/import/review" : "redirect:/tiwolij/data/import/report";
	}

	@GetMapping("review")
	public ModelAndView review() {
		ModelAndView mv = new ModelAndView("backend/data/review");

		if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
			return new ModelAndView("redirect:/tiwolij/data/import");
		}

		mv.addObject("results", session.getAttribute("results"));
		mv.addObject("errors", session.getAttribute("errors"));
		return mv;
	}

	@RequestMapping("report")
	public ModelAndView report(@RequestParam(value = "lines[]", defaultValue = "") Set<Integer> lines) {
		ModelAndView mv = new ModelAndView("backend/data/report");

		if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
			return new ModelAndView("redirect:/tiwolij/data/import");
		}

		List<Quote> imports = new ArrayList<Quote>();
		Map<Integer, Quote> results = (LinkedHashMap<Integer, Quote>) session.getAttribute("results");
		Map<Integer, Exception> errors = (LinkedHashMap<Integer, Exception>) session.getAttribute("errors");

		session.removeAttribute("results");
		session.removeAttribute("errors");

		List<Integer> lineList = new ArrayList<Integer>(results.keySet());
		Integer lastLine = lineList.get(lineList.size() - 1);

		if (lines.isEmpty()) {
			lines = results.keySet();
		}

		for (Integer i : lines) {
			try {
				session.setAttribute("progress", "import:" + (new Float(i) / lastLine * 100));

				Quote quote = results.get(i);
				Author author = quote.getAuthor();
				Work work = quote.getWork();

				Locale authorLocale = author.getLocales().iterator().next();
				Locale workLocale = work.getLocales().iterator().next();

				author.setLocales(null);
				work.setLocales(null);

				if (authors.existsByWikidataId(author.getWikidataId())) {
					author = authors.getOneByWikidataId(author.getWikidataId()).merge(author);
					author.addLocale(wikidata.getLocale(author.getWikidataId(), authorLocale.getLanguage()));
				} else if (authors.existsBySlug(author.getSlug())) {
					author = authors.getOneBySlug(author.getSlug()).merge(author);
					author.addLocale(authorLocale);
				} else {
					author.addLocale(authorLocale);
				}

				if (works.existsByWikidataId(work.getWikidataId())) {
					work = works.getOneByWikidataId(work.getWikidataId()).merge(work);
					work.addLocale(wikidata.getLocale(work.getWikidataId(), workLocale.getLanguage()));
				} else if (works.existsBySlug(work.getSlug())) {
					work = works.getOneBySlug(work.getSlug()).merge(work);
					work.addLocale(workLocale);
				} else {
					work.addLocale(workLocale);
				}

				entityManager.clear();
				author = authors.save(author);
				work = works.save(work.setAuthor(author));
				quote = quotes.save(quote.setWork(work));

				imports.add(quote);
			} catch (Exception e) {
				errors.put(i, e);
				e.printStackTrace();
			}
		}

		session.removeAttribute("progress");

		mv.addObject("quotes", imports);
		mv.addObject("errors", errors);
		return mv;
	}

	@ResponseBody
	@GetMapping("progress")
	public String progress(HttpServletResponse response) throws Exception {
		String progress = (String) session.getAttribute("progress");
		response.setContentType("text/plain");
		return (progress == null) ? "null" : progress;
	}

}
