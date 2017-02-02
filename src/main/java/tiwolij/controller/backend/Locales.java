package tiwolij.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Locale;
import tiwolij.service.author.AuthorService;
import tiwolij.service.locale.LocaleService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/tiwolij/locales")
public class Locales {

	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private LocaleService locales;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/locales/list";
	}

	@GetMapping("/list")
	public ModelAndView list(Pageable pageable, @RequestParam(name = "authorId", defaultValue = "0") Integer authorId,
			@RequestParam(name = "workId", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/locale/list");
		Page<Locale> page;

		if (authorId > 0 && authors.existsById(authorId)) {
			page = locales.getAllByAuthor(pageable, authorId);
		} else if (workId > 0 && works.existsById(workId)) {
			page = locales.getAllByWork(pageable, workId);
		} else {
			page = locales.getAll(pageable);
		}

		mv.addObject("locales", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/locale/view");

		mv.addObject("locale", locales.getOneById(localeId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId,
			@RequestParam(name = "workId", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/locale/create");
		Locale locale;

		if (authorId > 0 && authors.existsById(authorId)) {
			locale = new Locale(authors.getOneById(authorId));
		} else if (workId > 0 && works.existsById(workId)) {
			locale = new Locale(works.getOneById(workId));
		} else {
			locale = new Locale();
		}

		mv.addObject("locale", locale);
		mv.addObject("authors", authors.getAll());
		mv.addObject("works", works.getAll());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Locale locale, BindingResult errors) {
		// TODO: resolve model error on setting parent
		locales.save(locale);
		return "redirect:/tiwolij/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/locale/edit");

		mv.addObject("locale", locales.getOneById(localeId));
		mv.addObject("authors", authors.getAll());
		mv.addObject("works", works.getAll());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Locale locale) {
		locales.save(locale);
		return "redirect:/tiwolij/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "localeId") Integer localeId) {
		Locale locale = locales.getOneById(localeId);
		String redirect;

		if (locale.getAuthor() != null) {
			redirect = "redirect:/tiwolij/authors/view?authorId=" + locale.getAuthor().getId();
		} else if (locale.getWork() != null) {
			redirect = "redirect:/tiwolij/works/view?workId=" + locale.getWork().getId();
		} else {
			redirect = "redirect:/tiwolij/locales/list";
		}

		locales.delete(localeId);
		return redirect;
	}

}
