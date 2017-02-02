package tiwolij.controller.frontend;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;
import tiwolij.service.quote.QuoteService;

@Controller
@RequestMapping({ "", "/" })
public class Frontend {

	@Autowired
	private QuoteService quotes;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/view";
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "id", defaultValue = "0") Integer quoteId,
			@RequestParam(name = "schedule", defaultValue = "") String schedule) throws Exception {
		ModelAndView mv = new ModelAndView("frontend/view");
		String language = LocaleContextHolder.getLocale().getLanguage();

		if (quoteId > 0 && quotes.existsById(quoteId)) {
			schedule = quotes.getOneById(quoteId).getSchedule();
		}

		if (schedule.isEmpty()) {
			LocalDate now = LocalDate.now();
			String day = String.format("%02d", now.getDayOfMonth());
			String month = String.format("%02d", now.getMonthValue());
			schedule = day + "-" + month;
		}

		Map<String, Integer> languages = quotes.getAllBySchedule(schedule).stream()
				.collect(Collectors.toMap(Quote::getLanguage, i -> i.getId(), (x1, x2) -> {
					return x2;
				}));

		mv.addObject("lang", language);
		mv.addObject("langs", languages);
		mv.addObject("schedule", schedule);

		if (quoteId == 0) {
			if (!quotes.existsByScheduleAndLang(schedule, language)) {
				return mv;
			} else {
				quoteId = quotes.getRandomByScheduleAndLang(schedule, language).getId();
			}
		}

		Quote quote = quotes.getOneById(quoteId);
		Work work = quote.getWork();
		Author author = work.getAuthor();

		Quote prev = quotes.getRandomPrevBySibling(quoteId);
		Quote next = quotes.getRandomNextBySibling(quoteId);

		mv.addObject("quote", quote);
		mv.addObject("work", work.getMappedLocales().get(language));
		mv.addObject("author", author.getMappedLocales().get(language));
		mv.addObject("prev", (prev != null) ? prev.getId() : null);
		mv.addObject("next", (next != null) ? next.getId() : null);
		mv.addObject("list", quotes.getAllByScheduleAndLang(schedule, language));
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

		List<Quote> quoteList = quotes.getAllByScheduleAndLang(schedule, language);
		List<Triple<Quote, Locale, Locale>> list = new ArrayList<Triple<Quote, Locale, Locale>>();

		Locale w = null;
		Locale a = null;

		for (Quote i : quoteList) {
			w = i.getWork().getMappedLocales().get(language);
			a = i.getWork().getAuthor().getMappedLocales().get(language);
			list.add(Triple.of(i, w, a));
		}

		mv.addObject("list", list);
		mv.addObject("lang", language);
		mv.addObject("schedule", schedule);
		return mv;
	}

	@GetMapping("/random")
	public String random() {
		String language = LocaleContextHolder.getLocale().getLanguage();

		if (!quotes.existsByLang(language)) {
			return "redirect:/view?lang=" + language;
		}

		Quote random = quotes.getRandomByLang(language);
		return "redirect:/view?id=" + random.getId() + "&lang=" + language;
	}

	@GetMapping("/home")
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("frontend/home");
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

}
