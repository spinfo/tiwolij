package tiwolij.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping({ "/tiwolij", "/tiwolij/" })
public class Backend {

	@GetMapping({ "", "/" })
	public ModelAndView getRoot() {
		return new ModelAndView("backend");
	}

}
