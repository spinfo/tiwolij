package tiwolij.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import tiwolij.domain.Author;
import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.AuthorRepository;
import tiwolij.service.WorkLocaleRepository;
import tiwolij.service.WorkRepository;

@Controller
@RequestMapping("/tiwolij/works")
public class Works {

	@Autowired
	private AuthorRepository authors;
	@Autowired
	private WorkRepository works;
	@Autowired
	private WorkLocaleRepository locales;

	@GetMapping("/")
	public String getRoot() {
		return "redirect:/tiwolij/works/list";
	}

	@GetMapping("/list")
	public ModelAndView getList(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "author", defaultValue = "0") int author) throws Exception {
		List<Work> list = new ArrayList<Work>();

		if (author > 0) {
			list = works.findByAuthor(authors.findById(author));
		} else {
			list = works.findAll();
		}

		for (Work w : list)
			System.out.println(w.getLocales().size());

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		return new ModelAndView("works/works").addObject("works", list);
	}

	@GetMapping("/view")
	public ModelAndView getView(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("works/work");

		mv.addObject("work", works.findById(id));
		mv.addObject("authors", authors.findAll());
		mv.addObject("view", true);
		return mv;
	}

	@GetMapping("/edit")
	public ModelAndView getEdit(@RequestParam(name = "id", defaultValue = "0") int id) {
		ModelAndView mv = new ModelAndView("works/work");

		if (id > 0) {
			mv.addObject("work", works.findById(id));
		} else {
			mv.addObject("work", new Work());
		}

		mv.addObject("authors", authors.findAll());
		return mv;
	}

	@PostMapping("/edit")
	public String postEdit(@ModelAttribute Work work, @RequestParam(name = "id", defaultValue = "0") int id) {

		if (works.exists(id)) {
			work.setId(id);
			work.setQuotes(works.findById(id).getQuotes());
			work.setLocales(works.findById(id).getLocales());
		}

		works.save(work);
		return "redirect:/tiwolij/works/view?id=" + work.getId();
	}

	@PostMapping("/import")
	public String postImport(@ModelAttribute Work work) throws Exception {
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument(work.getWikidataId());

		if (item.hasStatement("P50")) {
			String id = item.findStatementItemIdValue("P50").getId();
			Author author = authors.findOneByWikidataId(id);

			if (author != null)
				work.setAuthor(author);
		}

		work.setSlug(item.getSiteLinks().get("enwiki").getPageTitle().toLowerCase().replace(" ", "_"));
		works.save(work);

		return "redirect:/tiwolij/works/view?id=" + work.getId();
	}

	@GetMapping("/delete")
	public String getDelete(@RequestParam(name = "id") int id) {
		works.delete(id);
		return "redirect:/tiwolij/works/list";
	}

	@GetMapping({ "/locales", "/locales/" })
	public String getLocalesRoot() {
		return "redirect:/tiwolij/works/locales/list";
	}

	@GetMapping("/locales/list")
	public ModelAndView getLocalesList(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "id", defaultValue = "0") int id) throws Exception {
		List<WorkLocale> list = new ArrayList<WorkLocale>();

		if (id > 0) {
			list = locales.findByWork(works.findById(id));
		} else {
			list = locales.findAll();
		}

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		return new ModelAndView("works/locales").addObject("locales", list);
	}

	@GetMapping("/locales/view")
	public ModelAndView getLocalesView(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("works/locale");

		mv.addObject("locale", locales.findById(id));
		mv.addObject("works", works.findAll());
		mv.addObject("view", true);

		return mv;
	}

	@GetMapping("/locales/edit")
	public ModelAndView getLocalesEdit(@RequestParam(name = "id", defaultValue = "0") int id,
			@RequestParam(name = "work", defaultValue = "0") int work) {
		ModelAndView mv = new ModelAndView("works/locale");

		if (id > 0) {
			mv.addObject("locale", locales.findById(id));
		} else if (work > 0) {
			mv.addObject("locale", new WorkLocale(works.findById(work)));
		} else {
			mv.addObject("locale", new WorkLocale());
		}

		mv.addObject("works", works.findAll());
		return mv;
	}

	@PostMapping("/locales/edit")
	public String postLocalesEdit(@ModelAttribute WorkLocale locale,
			@RequestParam(name = "id", defaultValue = "0") int id) {

		if (locales.exists(id)) {
			locale.setId(id);
		}

		locales.save(locale);
		return "redirect:/tiwolij/works/view?id=" + locale.getWork().getId();
	}

	@GetMapping("/locales/import")

	public ModelAndView getLocalesImport(@RequestParam(name = "id") int id) throws Exception {
		Work work = works.findById(id);
		ModelAndView mv = new ModelAndView("works/localize");
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument(work.getWikidataId());

		mv.addObject("work", work);
		mv.addObject("labels", item.getLabels());

		return mv;
	}

	@PostMapping("/locales/import")
	public String postLocalesImport(@RequestParam(name = "id") int id,
			@RequestParam(name = "labels[]") String[] selection) throws Exception {
		Work work = works.findById(id);
		WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
		ItemDocument item = (ItemDocument) data.getEntityDocument(work.getWikidataId());
		Map<String, MonolingualTextValue> labels = item.getLabels();

		for (String s : selection) {
			MonolingualTextValue label = labels.get(s);
			WorkLocale locale = new WorkLocale(work);

			locale.setLanguage(label.getLanguageCode());
			locale.setName(label.getText());
			locale.setHref(
					"https://" + label.getLanguageCode() + ".wikipedia.org/wiki/" + label.getText().replace(" ", "_"));

			locales.save(locale);
		}

		return "redirect:/tiwolij/works/view?id=" + work.getId();
	}

	@GetMapping("/locales/delete")
	public String getLocalesDelete(@RequestParam(name = "id") int id) {
		locales.delete(id);
		return "redirect:/tiwolij/works/locales/list";
	}

}
