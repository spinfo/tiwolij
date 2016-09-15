package tiwolij.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

	@GetMapping("/locales/delete")
	public String getLocalesDelete(@RequestParam(name = "id") int id) {
		locales.delete(id);
		return "redirect:/tiwolij/works/locales/list";
	}

}
