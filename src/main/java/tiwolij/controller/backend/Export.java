package tiwolij.controller.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import tiwolij.domain.QuoteLocale;
import tiwolij.service.quote.QuoteService;
import tiwolij.util.DataContainer;
import tiwolij.util.TiwoliChirp;

@Controller
@RequestMapping("/tiwolij/export")
public class Export {

	@Autowired
	private Environment env;

	@Autowired
	private QuoteService quotes;

	@Autowired
	private TiwoliChirp tiwoliChirp;

	@Autowired
	private MessageSource messages;

	@GetMapping("/tweets")
	public ModelAndView tweets() {
		ModelAndView mv = new ModelAndView("backend/export/tweets");
		Locale locale = LocaleContextHolder.getLocale();

		List<DataContainer> months = new ArrayList<DataContainer>();
		for (int i = 1; i <= 12; i++)
			months.add(new DataContainer(messages.getMessage("months." + String.format("%02d", i), null, locale),
					String.format("%02d", i)));

		mv.addObject("months", months);
		mv.addObject("formats", env.getProperty("tiwolij.export.format", String[].class));
		mv.addObject("languages", env.getProperty("tiwolij.localizations", String[].class));
		return mv;
	}

	@PostMapping("/tweets")
	public void tweets(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(name = "onlyfinal", defaultValue = "false") Boolean onlyfinal,
			@RequestParam(name = "onlylang", defaultValue = "") String onlylang,
			@RequestParam(name = "onlymonth", defaultValue = "") String onlymonth) throws Exception {

		List<QuoteLocale> locales = quotes.getLocales();
		String baseUrl = ServletUriComponentsBuilder.fromContextPath(request).build().toString();

		if (onlyfinal)
			locales = locales.stream().filter(l -> l.getLocked()).collect(Collectors.toList());

		if (!onlylang.isEmpty())
			locales = locales.stream().filter(l -> l.getLanguage().equals(onlylang)).collect(Collectors.toList());

		if (!onlymonth.isEmpty())
			locales = locales.stream().filter(l -> l.getMonth().equals(onlymonth)).collect(Collectors.toList());

		String tweets = tiwoliChirp.generateTwees(locales, baseUrl);

		response.setContentType("application/tsv");
		response.setHeader("Content-Disposition", "attachment; filename=tweets.tsv");
		response.getOutputStream().write(tweets.getBytes("UTF-8"));
		response.getOutputStream().close();

	}

}
