package tiwolij.controller.backend.data;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpSession;

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
import tiwolij.service.author.AuthorWikidataSource;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.tsv.TSVService;
import tiwolij.service.work.WorkService;
import tiwolij.service.work.WorkWikidataSource;
import tiwolij.util.LevenshteinDistance;

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
	private TSVService tsv;

	@Autowired
	EntityManager entityManager;

	@Autowired
	private AuthorWikidataSource authorImporter;

	@Autowired
	private WorkWikidataSource workImporter;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/data/import");

		mv.addObject("encodings", new String[] { "UTF-8", "UTF-16", "US-ASCII", "cp1252" });
		mv.addObject("formats", env.getProperty("tiwolij.import.formats", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@PostMapping({ "", "/" })
	public String root(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "forcelang", defaultValue = "") String forcelang,
			@RequestParam("format") String format, @RequestParam("encoding") String encoding,
			@RequestParam(name = "review", defaultValue = "false") Boolean review,
			@RequestParam(name = "heideltag", defaultValue = "false") Boolean heideltag) throws Exception {

		tsv.process(encoding, Arrays.asList(format.split(";")), forcelang, file.getBytes());
		session.setAttribute("import", tsv);

		return review ? "redirect:/tiwolij/data/import/review" : "redirect:/tiwolij/data/import/report";
	}

	@GetMapping("review")
	public ModelAndView review() {
		ModelAndView mv = new ModelAndView("backend/data/review");

		if (session.getAttribute("import") == null)
			return new ModelAndView("redirect:/tiwolij/data/import");

		tsv = (TSVService) session.getAttribute("import");

		mv.addObject("quotes", tsv.getResults());
		mv.addObject("errors", tsv.getErrors());
		return mv;
	}

	@RequestMapping("report")
	public ModelAndView report(@RequestParam(value = "lines[]", defaultValue = "") Set<Integer> lines) {
		ModelAndView mv = new ModelAndView("backend/data/report");

		if (session.getAttribute("import") == null)
			return new ModelAndView("redirect:/tiwolij/data/import");

		tsv = (TSVService) session.getAttribute("import");
		session.removeAttribute("import");

		Map<Integer, QuoteLocale> results = (LinkedHashMap<Integer, QuoteLocale>) tsv.getResults();
		Map<Integer, QuoteLocale> imports = new LinkedHashMap<Integer, QuoteLocale>();
		Map<Integer, Exception> errors = new LinkedHashMap<Integer, Exception>(tsv.getErrors());

		if (lines.isEmpty())
			lines = results.keySet();

		for (Integer line : lines) {
			try {
				imports.put(line, imp0rt(results.get(line)));
				entityManager.clear();
			} catch (Exception e) {
				errors.put(line, e);
				e.printStackTrace();
			}
		}

		mv.addObject("quotes", imports);
		mv.addObject("errors", errors);
		return mv;
	}

	private QuoteLocale imp0rt(QuoteLocale quoteLocale) throws Exception {
		String language = quoteLocale.getLanguage();

		for (QuoteLocale l : quotes.getLocalesByScheduleAndLang(quoteLocale.getSchedule(), language))
			if (LevenshteinDistance.get(l.getCorpus(), quoteLocale.getCorpus()) < Integer
					.parseInt(env.getProperty("tiwolij.import.levenshtein")))
				throw new DuplicateKeyException("Duplicate entry");

		Quote quote = quoteLocale.getQuote();
		Work work = quote.getWork();
		Author author = work.getAuthor();

		AuthorLocale authorLocale = author.getLocales().get(language);
		WorkLocale workLocale = work.getLocales().get(language);

		if (author.getWikidataId() != null) {
			if (authors.hasAuthorByWikidataId(author.getWikidataId())) {
				author = authors.getAuthorByWikidataId(author.getWikidataId());

				if (authors.hasLocale(author.getId(), language)) {
					authorLocale = authors.getLocaleByLang(author.getId(), language);
				} else {
					authorLocale = authorImporter.locale(author.getWikidataId(), language);
				}
			} else {
				author = authorImporter.byWikidataId(author.getWikidataId());
				authorLocale = authorImporter.locale(author.getWikidataId(), language);
			}
		}

		if (author.getId() == null && author.getSlug() != null) {
			if (authors.hasAuthorBySlug(author.getSlug())) {
				Author known = authors.getAuthorBySlug(author.getSlug());

				if (known.getWikidataId() == null && author.getWikidataId() != null) {
					work.setId(known.getId());
				} else {
					author = known;
				}

				if (authors.hasLocale(author.getId(), language)) {
					authorLocale = authors.getLocaleByLang(author.getId(), language);
				}
			}
		}

		if (work.getWikidataId() != null) {
			if (works.hasWorkByWikidataId(work.getWikidataId())) {
				work = works.getWorkByWikidataId(work.getWikidataId());

				if (works.hasLocale(work.getId(), language)) {
					workLocale = works.getLocaleByLang(work.getId(), language);
				} else {
					workLocale = workImporter.locale(work.getWikidataId(), language);
				}
			} else {
				work = workImporter.byWikidataId(work.getWikidataId());
				workLocale = workImporter.locale(work.getWikidataId(), language);
			}
		}

		if (work.getId() == null && work.getSlug() != null) {
			if (works.hasWorkBySlug(work.getSlug())) {
				Work known = works.getWorkBySlug(work.getSlug());

				if (known.getWikidataId() == null && work.getWikidataId() != null) {
					work.setId(known.getId());
				} else {
					work = known;
				}

				if (works.hasLocale(work.getId(), language)) {
					workLocale = works.getLocaleByLang(work.getId(), language);
				}
			}
		}

		authors.setAuthor(author);
		authors.setLocale(authorLocale.setAuthor(author));

		works.setWork(work.setAuthor(author));
		works.setLocale(workLocale.setWork(work));

		quotes.setQuote(quote.setWork(work));
		quotes.setLocale(quoteLocale.setQuote(quote));

		return quoteLocale;
	}
}
