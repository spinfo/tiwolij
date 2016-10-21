package tiwolij.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.domain.Quote;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping({ "", "/" })
public class Frontend {
	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public ModelAndView root() {
		return new ModelAndView("frontend/index");
	}

	@GetMapping("/about")
	public ModelAndView about() {
		return new ModelAndView("frontend/about");
	}

	@GetMapping("/list")
	public ModelAndView list(Locale locale, @RequestParam(name = "schedule", defaultValue = "") String schedule,
			@RequestParam(name = "lang", defaultValue = "") String language) {
		ModelAndView mv = new ModelAndView("frontend/list");

		if (schedule.isEmpty())
			schedule = LocalDate.now().getDayOfMonth() + "-" + LocalDate.now().getMonthValue();

		if (language.isEmpty())
			language = locale.getLanguage();

		List<QuoteLocale> locales = quotes.getLocalesByScheduleAndLang(schedule, language);
		List<Triple<QuoteLocale, WorkLocale, AuthorLocale>> list = new ArrayList<Triple<QuoteLocale, WorkLocale, AuthorLocale>>();

		WorkLocale w = null;
		AuthorLocale a = null;

		for (QuoteLocale q : locales) {
			w = works.getLocaleByLang(q.getQuote().getWork().getId(), language);
			a = authors.getLocaleByLang(w.getWork().getAuthor().getId(), language);
			list.add(Triple.of(q, w, a));
		}

		mv.addObject("lang", language);
		mv.addObject("list", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(Locale locale, @RequestParam(name = "id", defaultValue = "0") Integer quoteId,
			@RequestParam(name = "schedule", defaultValue = "") String schedule,
			@RequestParam(name = "lang", defaultValue = "") String language) throws Exception {
		ModelAndView mv = new ModelAndView("frontend/view");

		if (schedule.isEmpty())
			schedule = LocalDate.now().getDayOfMonth() + "-" + LocalDate.now().getMonthValue();

		if (language.isEmpty())
			language = locale.getLanguage();

		if (quoteId == 0) {
			if (quotes.hasLocaleByScheduleAndLang(schedule, language))
				quoteId = quotes.getLocaleByScheduleAndLang(schedule, language).getQuote().getId();
			else {
				QuoteLocale next = quotes.getLocaleNextByScheduleAndLang(schedule, language, false);
				QuoteLocale prev = quotes.getLocaleNextByScheduleAndLang(schedule, language, true);

				mv.addObject("lang", language);
				mv.addObject("next", next.getQuote().getId());
				mv.addObject("prev", prev.getQuote().getId());
				return mv;
			}
		}

		Quote quote = quotes.getQuote(quoteId);
		QuoteLocale quoteLocale = quotes.getLocaleByQuoteAndLang(quoteId, language);

		Work work = quote.getWork();
		WorkLocale workLocale = works.getLocaleByLang(work.getId(), language);

		Author author = work.getAuthor();
		AuthorLocale authorLocale = authors.getLocaleByLang(author.getId(), language);

		QuoteLocale next = quotes.getLocaleNextByScheduleAndLang(quoteLocale.getSchedule(), language, false);
		QuoteLocale prev = quotes.getLocaleNextByScheduleAndLang(quoteLocale.getSchedule(), language, true);

		mv.addObject("lang", language);
		mv.addObject("author", authorLocale);
		mv.addObject("work", workLocale);
		mv.addObject("quote", quoteLocale);
		mv.addObject("next", next.getQuote().getId());
		mv.addObject("prev", prev.getQuote().getId());
		return mv;
	}

	@GetMapping("/random")
	public String random(Locale locale) {
		QuoteLocale random = quotes.getLocaleRandomByLang(locale.getLanguage());
		return "redirect:/view?id=" + random.getQuote().getId() + "&lang=" + locale.getLanguage();
	}

}
