package tiwolij.controller.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import tiwolij.util.ImageLoader;

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

	@Autowired
	private WikidataService wikidata;

	@Autowired
	private TSVServiceImpl tsv;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView();

		return mv;
	}

	@PostMapping("/author")
	public String author(@ModelAttribute Author author) throws Exception {
		if (author.getWikidataId() <= 0) {
			throw new NoSuchEntityErrorException("No Wikidata ID given");
		}

		author = authors.save(importAuthor(author));
		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@PostMapping("/work")
	public String work(@ModelAttribute Work work) throws Exception {
		if (work.getWikidataId() <= 0) {
			throw new NoSuchEntityErrorException("No Wikidata ID given");
		}

		work = works.save(importWork(work));
		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

	@GetMapping("/locales")
	public String locales(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId,
			@RequestParam(name = "workId", defaultValue = "0") Integer workId) throws Exception {
		if (authorId > 0) {
			Author author = authors.getOneById(authorId);
			return "redirect:/tiwolij/authors/view?authorId=" + authors.save(importLocale(author)).getId();

		} else if (workId > 0) {
			Work work = works.getOneById(workId);
			return "redirect:/tiwolij/works/view?workId=" + works.save(importLocale(work)).getId();
		} else {
			return "redirect:/tiwolij/";
		}
	}

	@GetMapping("/tsv")
	public ModelAndView tsv() {
		ModelAndView mv = new ModelAndView("backend/data/import_tsv");

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		mv.addObject("formats", env.getProperty("tiwolij.import.formats", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@PostMapping("/tsv")
	public String tsv(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "forcelang", defaultValue = "") String forcelang,
			@RequestParam("format") String format, @RequestParam("encoding") String encoding,
			@RequestParam(name = "review", defaultValue = "false") Boolean review,
			@RequestParam(name = "heideltag", defaultValue = "false") Boolean heideltag,
			@RequestParam(name = "levensthein", defaultValue = "false") Boolean levensthein) throws Exception {
		session.setAttribute("progress", "pre");
		tsv.process(encoding, Arrays.asList(format.split(";")), forcelang, file.getBytes());

		Map<Integer, Quote> results = tsv.getResults();
		Map<Integer, Exception> errors = tsv.getErrors();

		if (heideltag) {
			Heideltimer timer = new Heideltimer();

			results.values().forEach(i -> {
				if (i.getYear() == null || i.getYear().isEmpty()) {
					i.setYear(timer.getYear(i));
				}

				if (i.getTime() == null || i.getTime().isEmpty()) {
					i.setTime(timer.getTime(i));
				}
			});
		}

		if (levensthein) {
			List<Quote> existing = quotes.getAll();
			Iterator<Entry<Integer, Quote>> quotes = results.entrySet().iterator();
			Integer distance = Integer.parseInt(env.getProperty("tiwolij.import.levenshtein"));

			while (quotes.hasNext()) {
				Entry<Integer, Quote> i = quotes.next();

				existing.stream().filter(j -> i.getValue().getSchedule().equals(j.getSchedule())).forEach(k -> {
					if (StringUtils.getLevenshteinDistance(i.getValue().getCorpus(), k.getCorpus()) < distance) {
						errors.put(i.getKey(), new DuplicateKeyException("Duplicate quote"));
						quotes.remove();
					}
				});
			}
		}

		session.setAttribute("results", results);
		session.setAttribute("errors", errors);

		return (review) ? "redirect:/tiwolij/data/import/review" : "redirect:/tiwolij/data/import/report";
	}

	@GetMapping("review")
	public ModelAndView review() {
		ModelAndView mv = new ModelAndView("backend/data/review");

		if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
			return new ModelAndView("redirect:/tiwolij/data/import/tsv");
		}

		mv.addObject("results", session.getAttribute("results"));
		mv.addObject("errors", session.getAttribute("errors"));
		return mv;
	}

	@RequestMapping("report")
	public ModelAndView report(@RequestParam(value = "lines[]", defaultValue = "") Set<Integer> lines) {
		ModelAndView mv = new ModelAndView("backend/data/report");

		if (session.getAttribute("results") == null && session.getAttribute("errors") == null) {
			return new ModelAndView("redirect:/tiwolij/data/import/tsv");
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
				session.setAttribute("progress", "" + (new Float(i) / lastLine * 100));

				Quote quote = results.get(i);
				Author author = importAuthor(quote.getAuthor());
				Work work = importWork(quote.getWork());

				authors.save(author);
				works.save(work.setAuthor(author));
				quotes.save(quote.setWork(work));

				imports.add(quote);
			} catch (Exception e) {
				errors.put(i, e);
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

	private Author importAuthor(Author author) throws Exception {
		Author existing = null;
		Locale locale = author.getLocales().iterator().next();
		Integer height = Integer.parseInt(env.getProperty("tiwolij.import.imageheight"));

		if (author.getWikidataId() != null) {
			author.setSlug(wikidata.extractSlug(author.getWikidataId()));
			locale = wikidata.extractLocale(author.getWikidataId(), locale.getLanguage());
		}

		if (authors.existsByWikidataId(author.getWikidataId())) {
			existing = authors.getOneByWikidataId(author.getWikidataId());
		} else if (authors.existsBySlug(author.getSlug())) {
			existing = authors.getOneBySlug(author.getSlug());
		}

		if (existing != null) {
			if (existing.getWikidataId() == null && author.getWikidataId() != null) {
				existing.setWikidataId(author.getWikidataId());
			}

			author = existing;
		} else {
			author.setImage(ImageLoader.getBytes(wikidata.extractImage(author.getWikidataId()), height));
			author.setImageAttribution(wikidata.extractImageAttribution(author.getWikidataId()));
		}

		return author.addLocale(locale);
	}

	private Work importWork(Work work) throws Exception {
		Work existing = null;
		Locale locale = work.getLocales().iterator().next();

		if (work.getWikidataId() != null) {
			work.setSlug(wikidata.extractSlug(work.getWikidataId()));
			locale = wikidata.extractLocale(work.getWikidataId(), locale.getLanguage());
		}

		if (works.existsByWikidataId(work.getWikidataId())) {
			existing = works.getOneByWikidataId(work.getWikidataId());
		} else if (works.existsBySlug(work.getSlug())) {
			existing = works.getOneBySlug(work.getSlug());
		}

		if (existing != null) {
			if (existing.getWikidataId() == null && work.getWikidataId() != null) {
				existing.setWikidataId(work.getWikidataId());
			}

			work = existing;
		}

		return work.addLocale(locale);
	}

	private Author importLocale(Author author) throws Exception {
		if (author.getWikidataId() != null) {
			for (String i : env.getProperty("tiwolij.locales.allowed", String[].class)) {
				if (!author.getMappedLocales().containsKey(i)) {
					author.addLocale(wikidata.extractLocale(author.getWikidataId(), i));
				}
			}
		}

		return author;
	}

	private Work importLocale(Work work) throws Exception {
		if (work.getWikidataId() == null) {
			for (String i : env.getProperty("tiwolij.locales.allowed", String[].class)) {
				if (!work.getMappedLocales().containsKey(i)) {
					work.addLocale(wikidata.extractLocale(work.getWikidataId(), i));
				}
			}
		}

		return work;
	}

}
