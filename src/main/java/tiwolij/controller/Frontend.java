package tiwolij.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
		ModelAndView mv = new ModelAndView("frontend/index");
		String language = LocaleContextHolder.getLocale().getLanguage();

		mv.addObject("lang", language);
		return mv;
	}

	@GetMapping("/about")
	public ModelAndView about() {
		ModelAndView mv = new ModelAndView("frontend/about");
		String language = LocaleContextHolder.getLocale().getLanguage();

		mv.addObject("lang", language);
		return mv;
	}

	@GetMapping("/list")
	public ModelAndView list(@RequestParam(name = "schedule", defaultValue = "") String schedule) {
		ModelAndView mv = new ModelAndView("frontend/list");
		String language = LocaleContextHolder.getLocale().getLanguage();

		if (schedule.isEmpty())
			schedule = String.format("%02d", LocalDate.now().getDayOfMonth()) + "-"
					+ String.format("%02d", LocalDate.now().getMonthValue());

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
	public ModelAndView view(@RequestParam(name = "id", defaultValue = "0") Integer quoteId,
			@RequestParam(name = "schedule", defaultValue = "") String schedule) throws Exception {
		ModelAndView mv = new ModelAndView("frontend/view");
		String language = LocaleContextHolder.getLocale().getLanguage();

		if (schedule.isEmpty())
			schedule = String.format("%02d", LocalDate.now().getDayOfMonth()) + "-"
					+ String.format("%02d", LocalDate.now().getMonthValue());

		if (quoteId == 0)
			if (quotes.hasLocaleByScheduleAndLang(schedule, language))
				quoteId = quotes.getLocaleRandomByScheduleAndLang(schedule, language).getQuote().getId();
			else {
				QuoteLocale next = quotes.getLocaleRandomNextByScheduleAndLang(schedule, language, false);
				QuoteLocale prev = quotes.getLocaleRandomNextByScheduleAndLang(schedule, language, true);

				mv.addObject("lang", language);
				mv.addObject("next", next != null ? next.getQuote().getId() : false);
				mv.addObject("prev", prev != null ? prev.getQuote().getId() : false);
				return mv;
			}

		Quote quote = quotes.getQuote(quoteId);
		QuoteLocale quoteLocale = quotes.getLocaleByQuoteAndLang(quoteId, language);

		Work work = quote.getWork();
		WorkLocale workLocale = works.getLocaleByLang(work.getId(), language);

		Author author = work.getAuthor();
		AuthorLocale authorLocale = authors.getLocaleByLang(author.getId(), language);

		QuoteLocale next = quotes.getLocaleRandomNextByScheduleAndLang(quoteLocale.getSchedule(), language, false);
		QuoteLocale prev = quotes.getLocaleRandomNextByScheduleAndLang(quoteLocale.getSchedule(), language, true);

		mv.addObject("lang", language);
		mv.addObject("author", authorLocale);
		mv.addObject("work", workLocale);
		mv.addObject("quote", quoteLocale);
		mv.addObject("next", next.getQuote().getId());
		mv.addObject("prev", prev.getQuote().getId());
		return mv;
	}

	@GetMapping("/random")
	public String random() {
		String language = LocaleContextHolder.getLocale().getLanguage();

		if (!quotes.hasLocaleByLang(language))
			return "redirect:/view?lang=" + language;

		QuoteLocale random = quotes.getLocaleRandomByLang(language);
		return "redirect:/view?id=" + random.getQuote().getId() + "&lang=" + language;
	}

}
