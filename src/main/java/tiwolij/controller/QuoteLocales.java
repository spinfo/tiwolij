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

import tiwolij.domain.Quote;
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
			@RequestParam(name = "quoteId", defaultValue = "0") Integer quoteId) throws Exception {
		ModelAndView mv = new ModelAndView("quoteLocales/list");
		List<QuoteLocale> list = (quotes.hasQuote(quoteId)) ? quotes.getLocalesByQuote(quoteId) : quotes.getLocales();
		list.sort((x, y) -> x.compareNaturalBy(y, order));

		mv.addObject("locales", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("quoteLocales/view");

		mv.addObject("locale", quotes.getLocale(localeId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "quoteId", defaultValue = "0") Integer quoteId) {
		ModelAndView mv = new ModelAndView("quoteLocales/create");
		QuoteLocale locale = (quotes.hasQuote(quoteId)) ? new QuoteLocale(quotes.getQuote(quoteId)) : new QuoteLocale();

		mv.addObject("locale", locale);
		mv.addObject("quotes", quotes.getQuotes());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute QuoteLocale locale) {
		quotes.setLocale(locale);
		return "redirect:/tiwolij/quotes/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("quoteLocales/edit");

		mv.addObject("locale", quotes.getLocale(localeId));
		mv.addObject("quotes", quotes.getQuotes());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute QuoteLocale locale) {
		quotes.setLocale(locale);
		return "redirect:/tiwolij/quotes/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "localeId") Integer localeId) {
		Quote quote = quotes.getLocale(localeId).getQuote();
		
		quotes.delLocale(localeId);
		return "redirect:/tiwolij/quotes/view?quoteId=" + quote.getId();
	}

}
