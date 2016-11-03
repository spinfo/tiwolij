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

		if (schedule.isEmpty()) {
			LocalDate now = LocalDate.now();
			String day = String.format("%02d", now.getDayOfMonth());
			String month = String.format("%02d", now.getMonthValue());
			schedule = day + "-" + month;
		}

		List<QuoteLocale> locales = quotes.getLocalesByScheduleAndLang(schedule, language);
		List<Triple<QuoteLocale, WorkLocale, AuthorLocale>> list = new ArrayList<Triple<QuoteLocale, WorkLocale, AuthorLocale>>();

		WorkLocale w = null;
		AuthorLocale a = null;

		for (QuoteLocale q : locales) {
			w = works.getLocaleByLang(q.getQuote().getWork().getId(), language);
			a = authors.getLocaleByLang(w.getWork().getAuthor().getId(), language);
			list.add(Triple.of(q, w, a));
		}

		mv.addObject("list", list);
		mv.addObject("lang", language);
		mv.addObject("schedule", schedule);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "id", defaultValue = "0") Integer quoteId,
			@RequestParam(name = "schedule", defaultValue = "") String schedule) throws Exception {
		ModelAndView mv = new ModelAndView("frontend/view");
		String language = LocaleContextHolder.getLocale().getLanguage();

		if (quoteId > 0 && quotes.hasLocale(quoteId, language))
			schedule = quotes.getLocaleByQuoteAndLang(quoteId, language).getSchedule();

		if (schedule.isEmpty()) {
			LocalDate now = LocalDate.now();
			String day = String.format("%02d", now.getDayOfMonth());
			String month = String.format("%02d", now.getMonthValue());
			schedule = day + "-" + month;
		}

		if (quoteId == 0 && !quotes.hasLocaleByScheduleAndLang(schedule, language))
			return mv.addObject("lang", language).addObject("schedule", schedule);

		if (quoteId == 0)
			quoteId = quotes.getLocaleRandomByScheduleAndLang(schedule, language).getId();

		QuoteLocale quoteLocale = quotes.getLocaleByQuoteAndLang(quoteId, language);
		mv.addObject("quote", quoteLocale);

		Work work = quoteLocale.getQuote().getWork();
		WorkLocale workLocale = works.getLocaleByLang(work.getId(), language);
		mv.addObject("work", workLocale);

		Author author = work.getAuthor();
		AuthorLocale authorLocale = authors.getLocaleByLang(author.getId(), language);
		mv.addObject("author", authorLocale);

		QuoteLocale prev = quotes.getLocaleRandomNextByScheduleAndLang(schedule, language, true);
		Integer prevId = prev != null ? prev.getQuote().getId() : null;
		mv.addObject("prevId", prevId);

		QuoteLocale next = quotes.getLocaleRandomNextByScheduleAndLang(schedule, language, false);
		Integer nextId = next != null ? next.getQuote().getId() : null;
		mv.addObject("nextId", nextId);

		mv.addObject("lang", language);
		mv.addObject("schedule", schedule);
		mv.addObject("list", quotes.getLocalesByScheduleAndLang(schedule, language));
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
