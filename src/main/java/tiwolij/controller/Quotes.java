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

import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;
import tiwolij.service.AuthorRepository;
import tiwolij.service.QuoteLocaleRepository;
import tiwolij.service.QuoteRepository;
import tiwolij.service.WorkRepository;

@Controller
@RequestMapping("/tiwolij/quotes")
public class Quotes {

	@Autowired
	private AuthorRepository authors;
	@Autowired
	private WorkRepository works;
	@Autowired
	private QuoteRepository quotes;
	@Autowired
	private QuoteLocaleRepository locales;

	@GetMapping({ "", "/" })
	public String getRoot() {
		return "redirect:/tiwolij/quotes/list";
	}

	@GetMapping("/list")
	public ModelAndView getList(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "author", defaultValue = "0") int author,
			@RequestParam(name = "work", defaultValue = "0") int work) throws Exception {
		List<Quote> list = new ArrayList<Quote>();

		if (author != 0) {
			for (Work w : works.findByAuthor(authors.findById(author)))
				list.addAll(quotes.findByWork(works.findById(w.getId())));
		} else if (work != 0) {
			list = quotes.findByWork(works.findById(work));
		} else {
			list = quotes.findAll();
		}

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		return new ModelAndView("quotes/quotes").addObject("quotes", list);
	}

	@GetMapping("/view")
	public ModelAndView getView(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("quotes/quote");

		mv.addObject("quote", quotes.findById(id));
		mv.addObject("works", works.findAll());
		mv.addObject("view", true);
		return mv;
	}

	@GetMapping("/edit")
	public ModelAndView getEdit(@RequestParam(name = "id", defaultValue = "0") int id) {
		ModelAndView mv = new ModelAndView("quotes/quote");

		if (id == 0) {
			mv.addObject("quote", new Quote());
		} else {
			mv.addObject("quote", quotes.findById(id));
		}

		mv.addObject("works", works.findAll());
		return mv;
	}

	@PostMapping("/edit")
	public String postEdit(@ModelAttribute Quote quote, @RequestParam(name = "id", defaultValue = "0") int id) {

		if (quotes.exists(id)) {
			quote.setId(id);
			quote.setLocales(quotes.findById(id).getLocales());
		}

		quotes.save(quote);
		return "redirect:/tiwolij/quotes/view?id=" + quote.getId();
	}

	@GetMapping("/delete")
	public String getDelete(@RequestParam(name = "id") int id) {
		quotes.delete(id);
		return "redirect:/tiwolij/quotes/list";
	}

	@GetMapping({ "/locales", "/locales/" })
	public String getLocalesRoot() {
		return "redirect:/tiwolij/quotes/locales/list";
	}

	@GetMapping("/locales/list")
	public ModelAndView getLocalesList(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "id", defaultValue = "0") int id) throws Exception {
		List<QuoteLocale> list = new ArrayList<QuoteLocale>();

		if (id == 0) {
			list = locales.findAll();
		} else {
			list = locales.findByQuote(quotes.findById(id));
		}

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		return new ModelAndView("quotes/locales").addObject("locales", list);
	}

	@GetMapping("/locales/view")
	public ModelAndView getLocalesView(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("quotes/locale");

		mv.addObject("locale", locales.findById(id));
		mv.addObject("quotes", quotes.findAll());
		mv.addObject("view", true);
		return mv;
	}

	@GetMapping("/locales/edit")
	public ModelAndView getLocalesEdit(@RequestParam(name = "id", defaultValue = "0") int id,
			@RequestParam(name = "quote", defaultValue = "0") int quote) {
		ModelAndView mv = new ModelAndView("quotes/locale");

		if (id > 0) {
			mv.addObject("locale", locales.findById(id));
		} else if (quote > 0) {
			mv.addObject("locale", new QuoteLocale(quotes.findById(quote)));
		} else {
			mv.addObject("locale", new QuoteLocale());
		}

		mv.addObject("quotes", quotes.findAll());
		return mv;
	}

	@PostMapping("/locales/edit")
	public String postLocalesEdit(@ModelAttribute QuoteLocale locale,
			@RequestParam(name = "id", defaultValue = "0") int id) {

		if (locales.exists(id)) {
			locale.setId(id);
		}

		locales.save(locale);
		return "redirect:/tiwolij/quotes/view?id=" + locale.getQuote().getId();
	}

	@GetMapping("/locales/delete")
	public String getLocalesDelete(@RequestParam(name = "id") int id) {
		locales.delete(id);
		return "redirect:/tiwolij/quotes/locales/list";
	}

}
