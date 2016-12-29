package tiwolij.controller.backend.author;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.service.author.AuthorWikidataSource;
import tiwolij.service.author.AuthorService;

@Controller
@RequestMapping("/tiwolij/authors/locales")
public class AuthorLocales {

	@Autowired
	private AuthorService authors;

	@Autowired
	private Environment env;

	@Autowired
	private AuthorWikidataSource importer;

	@PostMapping("/create")
	public String create(@ModelAttribute AuthorLocale locale) {
		authors.setLocale(locale);
		return "redirect:/tiwolij/authors/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/author/locale_create");
		AuthorLocale locale = (authors.hasAuthor(authorId)) ? new AuthorLocale(authors.getAuthor(authorId))
				: new AuthorLocale();

		mv.addObject("locale", locale);
		mv.addObject("authors", authors.getAuthors());
		return mv;
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "localeId") Integer localeId) {
		Author author = authors.getLocale(localeId).getAuthor();

		authors.delLocale(localeId);
		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute AuthorLocale locale) {
		authors.setLocale(locale);
		return "redirect:/tiwolij/authors/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/author/locale_edit");

		mv.addObject("locale", authors.getLocale(localeId));
		mv.addObject("authors", authors.getAuthors());
		return mv;
	}

	@GetMapping("/import")
	public String imp0rt(@RequestParam(name = "authorId") Integer authorId) throws Exception {
		Author author = authors.getAuthor(authorId);
		String[] allowedLanguages = env.getProperty("tiwolij.locales.allowed", String[].class);

		for (String language : allowedLanguages)
			if (!authors.hasLocale(authorId, language))
				authors.setLocale(importer.locale(author.getWikidataId(), language).setAuthor(author));

		return "redirect:/tiwolij/authors/view?authorId=" + authorId;
	}

	@GetMapping("/list")
	public ModelAndView list(Pageable pageable, @RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/author/locale_list");
		Page<AuthorLocale> page = authors.hasAuthor(authorId) ? authors.getLocalesByAuthor(pageable, authorId)
				: authors.getLocales(pageable);

		mv.addObject("locales", page);
		return mv;
	}

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/authors/locales/list";
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/author/locale_view");

		mv.addObject("locale", authors.getLocale(localeId));
		return mv;
	}

}
