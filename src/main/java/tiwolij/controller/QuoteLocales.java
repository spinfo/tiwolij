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

import tiwolij.domain.QuoteLocale;
import tiwolij.service.quote.QuoteService;

@Controller
@RequestMapping("/tiwolij/quotes/locales")
public class QuoteLocales {

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/quotes/locales/list";
	}

	@GetMapping("/list")
	public ModelAndView list(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "id", defaultValue = "0") int id) throws Exception {
		ModelAndView mv = new ModelAndView("quoteLocales/list");
		List<QuoteLocale> list = (quotes.hasQuote(id)) ? quotes.getLocalesByQuote(id) : quotes.getLocales();
		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));

		mv.addObject("locales", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("quoteLocales/view");

		mv.addObject("locale", quotes.getLocale(id));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "id", defaultValue = "0") int id) {
		ModelAndView mv = new ModelAndView("quoteLocales/create");
		QuoteLocale locale = (quotes.hasQuote(id)) ? new QuoteLocale(quotes.getQuote(id)) : new QuoteLocale();

		mv.addObject("locale", locale);
		mv.addObject("quotes", quotes.getQuotes());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute QuoteLocale locale) {
		quotes.setLocale(locale);
		return "redirect:/tiwolij/quotes/locales/view?id=" + locale.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "id") int id) {
		ModelAndView mv = new ModelAndView("quoteLocales/edit");

		mv.addObject("locale", quotes.getLocale(id));
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute QuoteLocale locale) {
		quotes.setLocale(locale);
		return "redirect:/tiwolij/quotes/locales/view?id=" + locale.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "id") int id) {
		quotes.delLocale(id);
		return "redirect:/tiwolij/quotes/locales/list";
	}

}
