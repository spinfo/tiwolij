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

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.service.AuthorLocaleRepository;
import tiwolij.service.AuthorRepository;

@Controller
@RequestMapping("/tiwolij/authors")
public class Authors {

	@Autowired
	private AuthorRepository authors;
	@Autowired
	private AuthorLocaleRepository locales;

	@GetMapping({ "", "/" })
	public String getRoot() {
		return "redirect:/tiwolij/authors/list";
	}

	@GetMapping("/list")
	public ModelAndView getList(@RequestParam(name = "order", defaultValue = "id") String order) throws Exception {
		List<Author> list = authors.findAll();

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		return new ModelAndView("authors/authors").addObject("authors", list);
	}

	@GetMapping("/view")
	public ModelAndView getView(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("authors/author");

		mv.addObject("author", authors.findById(id));
		mv.addObject("view", true);
		return mv;
	}

	@GetMapping("/edit")
	public ModelAndView getEdit(@RequestParam(name = "id", defaultValue = "0") int id) {
		ModelAndView mv = new ModelAndView("authors/author");

		if (id > 0) {
			mv.addObject("author", authors.findById(id));
		} else {
			mv.addObject("author", new Author());
		}

		return mv;
	}

	@PostMapping("/edit")
	public String postEdit(@ModelAttribute Author author, @RequestParam(name = "id", defaultValue = "0") int id) {

		if (authors.exists(id)) {
			author.setId(id);
			author.setWorks(authors.findById(id).getWorks());
			author.setLocales(authors.findById(id).getLocales());
		}

		authors.save(author);
		return "redirect:/tiwolij/authors/view?id=" + author.getId();
	}

	@GetMapping("/delete")
	public String getDelete(@RequestParam(name = "id") int id) {
		authors.delete(id);
		return "redirect:/tiwolij/authors/list";
	}

	@GetMapping({ "/locales", "/locales/" })
	public String getLocalesRoot() {
		return "redirect:/tiwolij/authors/locales/list";
	}

	@GetMapping("/locales/list")
	public ModelAndView getLocalesList(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "id", defaultValue = "0") int id) throws Exception {
		List<AuthorLocale> list = new ArrayList<AuthorLocale>();

		if (id > 0) {
			list = locales.findByAuthor(authors.findById(id));
		} else {
			list = locales.findAll();
		}

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		return new ModelAndView("authors/locales").addObject("locales", list);
	}

	@GetMapping("/locales/view")
	public ModelAndView getLocalesView(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("authors/locale");

		mv.addObject("locale", locales.findById(id));
		mv.addObject("authors", authors.findAll());
		mv.addObject("view", true);
		return mv;
	}

	@GetMapping("/locales/edit")
	public ModelAndView getLocalesEdit(@RequestParam(name = "id", defaultValue = "0") int id,
			@RequestParam(name = "author", defaultValue = "0") int author) {
		ModelAndView mv = new ModelAndView("authors/locale");

		if (id > 0) {
			mv.addObject("locale", locales.findById(id));
		} else if (author > 0) {
			mv.addObject("locale", new AuthorLocale(authors.findById(author)));
		} else {
			mv.addObject("locale", new AuthorLocale());
		}

		mv.addObject("authors", authors.findAll());
		return mv;
	}

	@PostMapping("/locales/edit")
	public String postLocalesEdit(@ModelAttribute AuthorLocale locale,
			@RequestParam(name = "id", defaultValue = "0") int id) {

		if (locales.exists(id)) {
			locale.setId(id);
		}

		locales.save(locale);
		return "redirect:/tiwolij/authors/view?id=" + locale.getAuthor().getId();
	}

	@GetMapping("/locales/delete")
	public String getLocalesDelete(@RequestParam(name = "id") int id) {
		locales.delete(id);
		return "redirect:/tiwolij/authors/locales/list";
	}

}
