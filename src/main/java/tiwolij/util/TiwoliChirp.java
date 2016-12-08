package tiwolij.util;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import tiwolij.domain.QuoteLocale;

@Component
public class TiwoliChirp {

	@Autowired
	private MessageSource messages;

	private String getTweetContent(QuoteLocale quoteLocale, String baseUrl) {
		String language = quoteLocale.getLanguage();
		Locale locale = new Locale(language);

		String prefix = messages.getMessage("data.export.tweets.prefix", null, locale);
		String day = messages.getMessage("day." + quoteLocale.getDay(), null, locale);
		String delim = messages.getMessage("months.delim", null, locale);
		String month = messages.getMessage("months." + quoteLocale.getMonth(), null, locale);
		String infix = messages.getMessage("data.export.tweets.infix", null, locale);
		String work = quoteLocale.getQuote().getWork().getLocales().get(language).getName();
		String author = quoteLocale.getQuote().getWork().getAuthor().getLocales().get(language).getName();
		String url = baseUrl + "/view?id=" + quoteLocale.getId() + "&lang=" + language;

		return prefix + day + delim + month + infix + author + ": " + work + ". #tiwoli " + url;

	}

	public String generateTwees(List<QuoteLocale> quoteLocales, String baseUrl) {
		StringBuffer sb = new StringBuffer();
		String text, date, time, imgUrl;

		if (messages == null)
			throw new BeanCreationException("Only callable when autowired");

		for (QuoteLocale q : quoteLocales) {
			imgUrl = baseUrl + "/image/flashcard?id=" + q.getId() + "&lang=" + q.getLanguage();

			text = getTweetContent(q, baseUrl);
			time = q.getTime() == null ? TimeRandomizer.getRandomizedTime() : q.getTime();
			date = q.getYear() == null
					? q.getSchedule().replace("-", ".") + "." + Calendar.getInstance().get(Calendar.YEAR)
					: q.getSchedule().replace("-", ".") + "." + q.getYear();

			sb.append(date + "\t" + time + "\t" + text + "\t" + imgUrl + "\t\t\n");
		}

		return sb.toString();
	}

}
