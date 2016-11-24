package tiwolij.util;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import tiwolij.domain.QuoteLocale;

@Component
public class TiwoliChirp {

	@Autowired
	private MessageSource messages;

	private Map<String, String> dayTimes = new HashMap<String, String>();

	public TiwoliChirp() {
		dayTimes.put("MO", "08:00");
		dayTimes.put("EV", "20:00");
		dayTimes.put("AF", "16:00");
		dayTimes.put("NI", "23:00");
		dayTimes.put("DT", "12:00");
	}

	public Language getLanguage(String language) {
		switch (language) {
		case "de":
			return Language.GERMAN;
		case "en":
			return Language.ENGLISH;
		case "es":
			return Language.SPANISH;
		default:
			return Language.WILDCARD;
		}
	}

	public String getYear(QuoteLocale quote, HeidelTimeWrapper ht) {
		String timeml;

		try {
			timeml = ht.process(quote.getCorpus());
		} catch (DocumentCreationTimeMissingException e) {
			e.printStackTrace();
			return null;
		}

		String[] split = quote.getSchedule().split("-");
		int day = Integer.parseInt(split[0]);
		int month = Integer.parseInt(split[1]);

		return parseYear(timeml, day, month);
	}

	public String getTime(QuoteLocale quote, HeidelTimeWrapper ht) {
		String timeml;

		try {
			timeml = ht.process(quote.getCorpus());
		} catch (DocumentCreationTimeMissingException e) {
			e.printStackTrace();
			return null;
		}

		String[] split = quote.getSchedule().split("-");
		int day = Integer.parseInt(split[0]);
		int month = Integer.parseInt(split[1]);
		String toReturn = parseTime(timeml, day, month);

		return toReturn;
	}

	private String parseYear(String string, int day, int month) {
		String regex = "type=\"(DATE|TIME)\" value=\"([0-9]{4})-" + month + "-" + day;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);

		if (matcher.find()) {
			// years.put(Integer.parseInt(matcher.group(2)), string);
			System.out.println(matcher.group(2));
			return matcher.group(2);
		}

		regex = "type=\"(DATE|TIME)\" value=\"([0-9]{4})-";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(string);

		if (matcher.find()) {
			// years.put(Integer.parseInt(matcher.group(2)), string);
			System.out.println(matcher.group(2));
			return matcher.group(2);
		}

		return null;
	}

	private String parseTime(String string, int day, int month) {
		String regex = "type=\"TIME\" value=\"[0-9|X]{4}-month-dayT([0-9|A-Z|:]+)\">";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);

		if (matcher.find()) {
			// times.put(matcher.group(1), string);
			System.out.println(matcher.group(1));
			return matcher.group(1);
		}

		regex = "type=\"TIME\" value=\"[0-9|X]{4}-[0-9|X]{2}-[0-9|X]{2}T([0-9|A-Z|:]+)\">";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(string);

		if (matcher.find()) {
			// times.put(matcher.group(1),string);
			System.out.println(matcher.group(1));
			return matcher.group(1);
		}

		return null;
	}

	private String getTweetTime(String time) {
		if (dayTimes.get(time) != null) {
			return dayTimes.get(time);
		}
		return time;
	}

	private String getTweetContent(QuoteLocale quoteLocale, String baseUrl) {
		String language = quoteLocale.getLanguage();
		Locale locale = new Locale(language);

		String start = messages.getMessage("data.export.tweet.start", null, locale);
		String delim = messages.getMessage("months.delim", null, locale);
		String month = messages.getMessage("months." + quoteLocale.getMonth(), null, locale);
		String tiwoli = messages.getMessage("data.export.tiwoli", null, locale);
		String url = baseUrl + "/view?id=" + quoteLocale.getId() + "&lang=" + language;
		String work = quoteLocale.getQuote().getWork().getLocales().get(language).getName();
		String author = quoteLocale.getQuote().getWork().getAuthor().getLocales().get(language).getName();

		String day = quoteLocale.getDay(); 
		if(day.charAt(0)=='0'){
			day=day.substring(1);
		}
		
		return start + " " + day + delim + month + tiwoli + author + ": " + work + ". #tiwoli " + url;
	}

	public String generateTwees(List<QuoteLocale> quoteLocales, String baseUrl) {
		StringBuffer sb = new StringBuffer();
		String text;
		String date;
		String time;
		String imgUrl;

		if (messages == null)
			throw new BeanCreationException("Only callable when autowired");

		for (QuoteLocale q : quoteLocales) {
			imgUrl = baseUrl + "/image/flashcard?id=" + q.getId() + "&lang=" + q.getLanguage();

			text = getTweetContent(q, baseUrl);
			time = getTweetTime(q.getTime() == null ? TimeRandomizer.getRandomizedTime() : q.getTime());
			date = q.getYear() == null ? Calendar.getInstance().get(Calendar.YEAR) + q.getSchedule()
					: q.getYear() + "-" + q.getSchedule();

			sb.append(date + "\t" + time + "\t" + text + "\t" + imgUrl + "\t\t\n");
		}

		return sb.toString();
	}

}
