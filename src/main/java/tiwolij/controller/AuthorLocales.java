package tiwolij.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.AuthorLocale;
import tiwolij.service.author.AuthorService;

@Controller
@RequestMapping("/tiwolij/authors/locales")
public class AuthorLocales {

	@Autowired
	private AuthorService authors;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/authors/locales/list";
	}

	@GetMapping("/list")
	public ModelAndView list(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "authorId", defaultValue = "0") Integer authorId) throws Exception {
		ModelAndView mv = new ModelAndView("authorLocales/list");
		List<AuthorLocale> list = (authors.hasAuthor(authorId)) ? authors.getLocalesByAuthor(authorId)
				: authors.getLocales();
		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));

		mv.addObject("locales", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("authorLocales/view");

		mv.addObject("locale", authors.getLocale(id));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("authorLocales/create");
		AuthorLocale locale = (authors.hasAuthor(authorId)) ? new AuthorLocale(authors.getAuthor(authorId)) : new AuthorLocale();

		mv.addObject("locale", locale);
		mv.addObject("authors", authors.getAuthors());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute AuthorLocale locale) {
		authors.setLocale(locale);
		return "redirect:/tiwolij/authors/locales/view?id=" + locale.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("authorLocales/edit");

		mv.addObject("locale", authors.getLocale(id));
		mv.addObject("authors", authors.getAuthors());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute AuthorLocale locale) {
		authors.setLocale(locale);
		return "redirect:/tiwolij/authors/locales/view?id=" + locale.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "id") int id) {
		authors.delLocale(id);
		return "redirect:/tiwolij/authors/locales/list";
	}

}
