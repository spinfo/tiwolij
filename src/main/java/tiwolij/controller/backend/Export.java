package tiwolij.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.service.quote.QuoteService;

@Controller
@RequestMapping("/tiwolij/export")
public class Export {

	@Autowired
	private Environment env;

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/export/export");

		mv.addObject("formats", env.getProperty("tiwolij.export.format", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.localizations", String[].class));
		return mv;
	}

}
