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
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/tiwolij/quotes")
public class Quotes {

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/quotes/list";
	}

	@GetMapping("/list")
	public ModelAndView list(Pageable pageable, @RequestParam(name = "authorId", defaultValue = "0") Integer authorId,
			@RequestParam(name = "workId", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/quote/quote_list");
		Page<Quote> page = works.hasWork(workId) ? quotes.getQuotesByWork(pageable, workId)
				: quotes.getQuotes(pageable);

		mv.addObject("quotes", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "quoteId") Integer quoteId) {
		ModelAndView mv = new ModelAndView("backend/quote/quote_view");

		mv.addObject("quote", quotes.getQuote(quoteId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "workId", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/quote/quote_create");
		Quote quote = (works.hasWork(workId)) ? new Quote(works.getWork(workId)) : new Quote();

		mv.addObject("quote", quote);
		mv.addObject("works", works.getWorks());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Quote quote) throws Exception {
		quote = quotes.setQuote(quote);
		return "redirect:/tiwolij/quotes/view?quoteId=" + quote.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "quoteId") Integer quoteId) {
		ModelAndView mv = new ModelAndView("backend/quote/quote_edit");

		mv.addObject("quote", quotes.getQuote(quoteId));
		mv.addObject("works", works.getWorks());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Quote quote) throws Exception {
		return "redirect:/tiwolij/quotes/view?quoteId=" + quotes.setQuote(quote).getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "quoteId") Integer quoteId) {
		quotes.delQuote(quoteId);
		return "redirect:/tiwolij/quotes/list";
	}

}