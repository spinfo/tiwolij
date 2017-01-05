package tiwolij.controller.backend.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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

import tiwolij.domain.QuoteLocale;
import tiwolij.service.quote.QuoteService;
import tiwolij.util.DataContainer;
import tiwolij.util.Heideltimer;

@Controller
@RequestMapping("/tiwolij/data/heideltag")
public class Heideltag {

	@Autowired
	private Environment env;

	@Autowired
	private QuoteService quotes;

	@Autowired
	private MessageSource messages;

	private Heideltimer ht = new Heideltimer();

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		ModelAndView mv = new ModelAndView("backend/data/heideltag");
		Locale locale = LocaleContextHolder.getLocale();

		List<DataContainer> months = new ArrayList<DataContainer>();
		for (int i = 1; i <= 12; i++)
			months.add(new DataContainer(messages.getMessage("months." + String.format("%02d", i), null, locale),
					String.format("%02d", i)));

		mv.addObject("months", months);
		mv.addObject("languages", env.getProperty("tiwolij.locales.allowed", String[].class));
		return mv;
	}

	@PostMapping({ "", "/" })
	public ModelAndView root(@RequestParam(name = "onlyfinal", defaultValue = "false") Boolean onlyfinal,
			@RequestParam(name = "onlylang", defaultValue = "") String onlylang,
			@RequestParam(name = "onlymonth", defaultValue = "") String onlymonth) throws Exception {
		ModelAndView mv = new ModelAndView("backend/data/report");

		String year, time;
		List<QuoteLocale> locales = quotes.getLocales();
		Map<Integer, QuoteLocale> tagged = new HashMap<Integer, QuoteLocale>();
		Map<Integer, QuoteLocale> errors = new HashMap<Integer, QuoteLocale>();

		if (onlyfinal)
			locales = locales.stream().filter(l -> l.getLocked()).collect(Collectors.toList());

		if (!onlylang.isEmpty())
			locales = locales.stream().filter(l -> l.getLanguage().equals(onlylang)).collect(Collectors.toList());

		if (!onlymonth.isEmpty())
			locales = locales.stream().filter(l -> l.getMonth().equals(onlymonth)).collect(Collectors.toList());

		for (QuoteLocale locale : locales) {
			year = locale.getYear();
			time = locale.getTime();

			if (year == null || year.isEmpty())
				locale.setYear(ht.getYear(locale));

			if (time == null || time.isEmpty())
				locale.setTime(ht.getTime(locale));

			if (year != locale.getYear() || time != locale.getTime())
				tagged.put(locale.getId(), quotes.setLocale(locale));
		}

		mv.addObject("quotes", tagged);
		mv.addObject("errors", errors);
		return mv;
	}

}
