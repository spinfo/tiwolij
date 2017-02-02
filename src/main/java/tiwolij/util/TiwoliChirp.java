package tiwolij.util;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import tiwolij.domain.Quote;

@Component
public class TiwoliChirp {

	@Autowired
	private MessageSource messages;

	public String generateTwees(List<Quote> quotes, String baseUrl) {
		StringBuffer sb = new StringBuffer();
		String text, date, time, imgUrl;

		for (Quote i : quotes) {
			imgUrl = baseUrl + "/image/flashcard?id=" + i.getId() + "&lang=" + i.getLanguage();

			text = getTweetContent(i, baseUrl);
			time = (i.getTime() == null) ? TimeRandomizer.getRandomizedTime() : i.getTime();
			date = (i.getYear() == null)
					? i.getSchedule().replace("-", ".") + "." + Calendar.getInstance().get(Calendar.YEAR)
					: i.getSchedule().replace("-", ".") + "." + i.getYear();

			sb.append(date + "\t" + time + "\t" + text + "\t" + imgUrl + "\t\t\n");
		}

		return sb.toString();
	}

	private String getTweetContent(Quote quote, String baseUrl) {
		String language = quote.getLanguage();
		Locale locale = new Locale(language);

		String prefix = messages.getMessage("data.export.tweets.prefix", null, locale);
		String day = messages.getMessage("day." + quote.getDay(), null, locale);
		String delim = messages.getMessage("months.delim", null, locale);
		String month = messages.getMessage("months." + quote.getMonth(), null, locale);
		String infix = messages.getMessage("data.export.tweets.infix", null, locale);
		String work = quote.getWork().getMappedLocales().get(language).getName();
		String author = quote.getAuthor().getMappedLocales().get(language).getName();
		String url = baseUrl + "/view?id=" + quote.getId() + "&lang=" + language;

		return prefix + day + delim + month + infix + author + ": " + work + ". #tiwoli " + url;
	}

}
