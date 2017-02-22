package tiwolij.controller.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import tiwolij.domain.Quote;
import tiwolij.service.quote.QuoteService;
import tiwolij.util.TiwoliChirp;

@Controller
@RequestMapping("/tiwolij/data/export")
public class Export {

	@Autowired
	private Environment env;

	@Autowired
	private QuoteService quotes;

	@Autowired
	private MessageSource messages;

	@Autowired
	private TiwoliChirp chirper;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/data/export");

		return mv;
	}

	@GetMapping("/json")
	public ModelAndView json() {
		ModelAndView mv = new ModelAndView("backend/data/export_json");
		Locale locale = LocaleContextHolder.getLocale();

		List<Pair<String, String>> months = new ArrayList<Pair<String, String>>();
		for (Integer i = 1; i <= 12; i++) {
			months.add(Pair.of(String.format("%02d", i),
					messages.getMessage("months." + String.format("%02d", i), null, locale)));
		}

		mv.addObject("months", months);
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@RequestMapping(value = "/json", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody List<Quote> json(HttpServletResponse response,
			@RequestParam(name = "onlyfinal", defaultValue = "false") Boolean onlyfinal,
			@RequestParam(name = "onlylang", defaultValue = "") String onlylang,
			@RequestParam(name = "onlymonth", defaultValue = "") String onlymonth) throws Exception {

		List<Quote> list = quotes.getAll();

		if (onlyfinal) {
			list = list.stream().filter(i -> i.getLocked()).collect(Collectors.toList());
		}

		if (!onlylang.isEmpty()) {
			list = list.stream().filter(i -> i.getLanguage().equals(onlylang)).collect(Collectors.toList());
		}

		if (!onlymonth.isEmpty()) {
			list = list.stream().filter(i -> i.getMonth().equals(onlymonth)).collect(Collectors.toList());
		}

		response.setContentType("application/json");
		response.setHeader("Content-Disposition", "attachment; filename=tiwolij.json");
		return list;
	}

	@GetMapping("/tweets")
	public ModelAndView tweets() {
		ModelAndView mv = new ModelAndView("backend/data/export_tweets");
		Locale locale = LocaleContextHolder.getLocale();

		List<Pair<String, String>> months = new ArrayList<Pair<String, String>>();
		for (Integer i = 1; i <= 12; i++) {
			months.add(Pair.of(String.format("%02d", i),
					messages.getMessage("months." + String.format("%02d", i), null, locale)));
		}

		mv.addObject("months", months);
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@PostMapping("/tweets")
	public void tweets(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "onlyfinal", defaultValue = "false") Boolean onlyfinal,
			@RequestParam(name = "onlylang", defaultValue = "") String onlylang,
			@RequestParam(name = "onlymonth", defaultValue = "") String onlymonth) throws Exception {

		List<Quote> list = quotes.getAll();
		String baseUrl = ServletUriComponentsBuilder.fromContextPath(request).build().toString();

		if (onlyfinal) {
			list = list.stream().filter(i -> i.getLocked()).collect(Collectors.toList());
		}

		if (!onlylang.isEmpty()) {
			list = list.stream().filter(i -> i.getLanguage().equals(onlylang)).collect(Collectors.toList());
		}

		if (!onlymonth.isEmpty()) {
			list = list.stream().filter(i -> i.getMonth().equals(onlymonth)).collect(Collectors.toList());
		}

		String tweets = chirper.generateTwees(list, baseUrl);

		response.setContentType("application/tsv");
		response.setHeader("Content-Disposition", "attachment; filename=tweets.tsv");
		response.getOutputStream().write(tweets.getBytes("UTF-8"));
		response.getOutputStream().close();
	}

}
