package tiwolij.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;

@Component
public class TiwoliChirp {

	@Autowired
	private MessageSource messages;

	private Map<String, String> dayTimes = new HashMap<String, String>();

	// private Map<String,String> months = new HashMap<String,String>();
	// private Map<String, String> times = new HashMap<String, String>();
	// private Map<Integer, String> years = new HashMap<Integer, String>();

	public TiwoliChirp() {
		dayTimes.put("MO", "08:00");
		dayTimes.put("EV", "20:00");
		dayTimes.put("AF", "16:00");
		dayTimes.put("NI", "23:00");
		dayTimes.put("DT", "12:00");
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

	public Language getLanguage(String language) {
		if (language.equals("de"))
			return Language.GERMAN;
		if (language.equals("en"))
			return Language.ENGLISH;
		if (language.equals("es"))
			return Language.SPANISH;
		else {
			System.out.println("unknown language: " + language);
			return Language.WILDCARD;
		}
	}

	public String generateTwees(List<QuoteLocale> quotes) {
		StringBuffer sb = new StringBuffer();
		String tweet;
		String date;
		String time;
		String imgUrl;
		String url;
		String lang;

		for (QuoteLocale quote : quotes) {
			lang = quote.getLanguage();
			url = "/view?id=" + quote.getId() + "&lang=" + lang;
			imgUrl = "/image/flashcard?id=" + quote.getId() + "&lang=" + lang;

			if (quote.getYear() != null) {
				date = quote.getYear() + "-" + quote.getSchedule();
			} else {
				date = "0000-" + quote.getSchedule();
			}

			tweet = getTweetContent(quote.getQuote().getWork(), lang, quote.getDay(), quote.getMonth(), url);

			if (quote.getTime() == null) {
				time = TimeRandomizer.getRandomizedTime();
			}

			time = getTweetTime(quote.getTime());
			sb.append(date + "\t" + time + "\t" + tweet + "\t" + imgUrl + "\t\t\n");
		}

		return sb.toString();
	}

	private String getTweetTime(String time) {
		if (dayTimes.get(time) != null) {
			return dayTimes.get(time);
		}
		return time;
	}

	private String getTweetContent(Work work, String lang, String day, String month, String url) {
		StringBuffer sb = new StringBuffer();
		month = messages.getMessage("months." + month, null, new Locale(lang));

		if (lang.equals("de")) {
			sb.append("Der " + day + ". " + month + " in der Weltliteratur: ");
		}
		if (lang.equals("en")) {
			sb.append(month + " " + day + " in world literatur: ");
		}
		if (lang.equals("es")) {
			// TODO translate
			sb.append(month + " " + day + " in world literatur: ");
		}

		sb.append(work.getAuthor().getLocales().get(lang) + " " + work.getAuthor().getLocales().get(lang) + ": "
				+ work.getLocales().get(lang) + ". #tiwoli " + url);

		return sb.toString();
	}
}
