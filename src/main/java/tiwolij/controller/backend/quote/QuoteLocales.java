package tiwolij.controller.backend.quote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public ModelAndView list(Pageable pageable, @RequestParam(name = "quoteId", defaultValue = "0") Integer quoteId) {
		ModelAndView mv = new ModelAndView("backend/quote/locale_list");
		Page<QuoteLocale> page = quotes.hasQuote(quoteId) ? quotes.getLocalesByQuote(pageable, quoteId)
				: quotes.getLocales(pageable);

		mv.addObject("locales", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/quote/locale_view");

		mv.addObject("locale", quotes.getLocale(localeId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "quoteId", defaultValue = "0") Integer quoteId) {
		ModelAndView mv = new ModelAndView("backend/quote/locale_create");
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
		ModelAndView mv = new ModelAndView("backend/quote/locale_edit");

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
