package tiwolij.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.service.quote.QuoteService;

@Controller
@RequestMapping({ "", "/" })
public class Frontend {

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public String getRoot() {
		return "redirect:/view";
	}

	@GetMapping("/view")
	public ModelAndView getView(@RequestParam(name = "id", defaultValue = "0") int id) {
		return new ModelAndView("frontend").addObject("quote", quotes.getQuote(id));
	}

}
