package tiwolij.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping({ "/tiwolij", "/tiwolij/" })
public class Backend {


	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public ModelAndView getRoot() {
		ModelAndView mv = new ModelAndView("backend/index");

		mv.addObject("authors", authors.count());
		mv.addObject("authorLocales", authors.countLocales());
		mv.addObject("works", works.count());
		mv.addObject("workLocales", works.countLocales());
		mv.addObject("quotes", quotes.getCount());
		mv.addObject("quoteLocales", quotes.getLocaleCount());
		return mv;
	}
	
	@GetMapping("search")
	public ModelAndView search(@RequestParam("term") String term) {
		ModelAndView mv = new ModelAndView("backend/search");
		mv.addObject("authors", authors.search(term));
		mv.addObject("works", works.search(term));
		mv.addObject("quotes", quotes.search(term));
		return mv;
	}

}
