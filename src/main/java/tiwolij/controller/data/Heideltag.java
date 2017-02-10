package tiwolij.controller.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Quote;
import tiwolij.service.quote.QuoteService;
import tiwolij.util.Heideltimer;

@Controller
@RequestMapping("/tiwolij/data/heideltag")
public class Heideltag {

	@Autowired
	private Environment env;

	@Autowired
	private HttpSession session;

	@Autowired
	private QuoteService quotes;

	@Autowired
	private MessageSource messages;

	private Heideltimer timer = new Heideltimer();

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/data/heideltag");
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

	@PostMapping({ "", "/" })
	public ModelAndView root(@RequestParam(name = "onlyfinal", defaultValue = "false") Boolean onlyfinal,
			@RequestParam(name = "onlylang", defaultValue = "") String onlylang,
			@RequestParam(name = "onlymonth", defaultValue = "") String onlymonth) throws Exception {
		ModelAndView mv = new ModelAndView("backend/data/report");
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

		Iterator<Quote> iter = list.iterator();
		List<Quote> tagged = new ArrayList<Quote>();

		String year, time;
		Integer count = 0;
		Integer total = list.size();
		session.setAttribute("progress", "heideltag:" + count);

		while (iter.hasNext()) {
			session.setAttribute("progress", "heideltag:" + (new Float(count++) / total * 100));
			Quote i = iter.next();

			year = i.getYear();
			time = i.getTime();

			i.setYear((!i.hasYear()) ? timer.getYear(i) : i.getYear());
			i.setTime((!i.hasTime()) ? timer.getTime(i) : i.getTime());

			if (year != i.getYear() || time != i.getTime()) {
				tagged.add(quotes.save(i));
			}
		}

		mv.addObject("quotes", tagged);
		return mv;
	}

}
