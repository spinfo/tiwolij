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
import tiwolij.domain.Work;
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
		return "redirect:/tiwolij/authors/list";
	}

	@GetMapping("/list")
	public ModelAndView list(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "authorId", defaultValue = "0") Integer authorId,
			@RequestParam(name = "workId", defaultValue = "0") Integer workId) throws Exception {
		ModelAndView mv = new ModelAndView("quotes/list");
		List<Quote> list = new ArrayList<Quote>();

		if (authorId != 0) {
			for (Work w : works.getWorksByAuthor(authorId))
				list.addAll(quotes.getQuotesByWork(w.getId()));
		} else if (workId != 0) {
			list = quotes.getQuotesByWork(workId);
		} else {
			list = quotes.getQuotes();
		}

		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));
		mv.addObject("quotes", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "id") Integer id) {
		ModelAndView mv = new ModelAndView("quotes/view");

		mv.addObject("quote", quotes.getQuote(id));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create() {
		ModelAndView mv = new ModelAndView("quotes/create");

		mv.addObject("quote", new Quote());
		mv.addObject("works", works.getWorks());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Quote quote) throws Exception {
		return "redirect:/tiwolij/authors/view?id=" + quotes.setQuote(quote).getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "id") Integer id) {
		ModelAndView mv = new ModelAndView("quotes/edit");

		mv.addObject("quote", quotes.getQuote(id));
		mv.addObject("works", works.getWorks());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Quote quote) throws Exception {
		return "redirect:/tiwolij/quotes/view?id=" + quotes.setQuote(quote).getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "id") Integer id) {
		quotes.delQuote(id);
		return "redirect:/tiwolij/quotes/list";
	}

}
