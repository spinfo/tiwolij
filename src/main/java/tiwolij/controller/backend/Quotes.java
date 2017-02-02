package tiwolij.controller.backend;

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
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/tiwolij/quotes")
public class Quotes {

	@Autowired
	private AuthorService authors;

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
		ModelAndView mv = new ModelAndView("backend/quote/list");
		Page<Quote> page;

		if (authorId > 0 && authors.existsById(authorId)) {
			page = quotes.getAllByAuthor(pageable, authorId);
		} else if (workId > 0 && works.existsById(workId)) {
			page = quotes.getAllByWork(pageable, workId);
		} else {
			page = quotes.getAll(pageable);
		}

		mv.addObject("page", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "quoteId") Integer quoteId) {
		ModelAndView mv = new ModelAndView("backend/quote/view");

		mv.addObject("quote", quotes.getOneById(quoteId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "workId", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/quote/create");
		Quote quote;

		if (works.existsById(workId)) {
			quote = new Quote(works.getOneById(workId));
		} else {
			quote = new Quote();
		}

		mv.addObject("quote", quote);
		mv.addObject("works", works.getAll());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Quote quote) throws Exception {
		quote = quotes.save(quote);
		return "redirect:/tiwolij/quotes/view?quoteId=" + quote.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "quoteId") Integer quoteId) {
		ModelAndView mv = new ModelAndView("backend/quote/edit");

		mv.addObject("quote", quotes.getOneById(quoteId));
		mv.addObject("works", works.getAll());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Quote quote) throws Exception {
		return "redirect:/tiwolij/quotes/view?quoteId=" + quotes.save(quote).getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "quoteId") Integer quoteId) {
		quotes.delete(quoteId);
		return "redirect:/tiwolij/quotes/list";
	}

}
