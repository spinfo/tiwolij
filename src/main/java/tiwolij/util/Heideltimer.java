package tiwolij.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import tiwolij.domain.QuoteLocale;

public class Heideltimer {

	private Map<String, String> dayTimes = new HashMap<String, String>();

	private Map<String, HeidelTimeWrapper> htWrappers = new HashMap<String, HeidelTimeWrapper>();

	public Heideltimer() {
		dayTimes.put("MO", "08:00");
		dayTimes.put("EV", "20:00");
		dayTimes.put("AF", "16:00");
		dayTimes.put("NI", "23:00");
		dayTimes.put("DT", "12:00");
	}

	public String getDayTime(String time) {
		if (dayTimes.containsKey(time))
			return dayTimes.get(time);

		return time;
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

	public String getYear(QuoteLocale quote) {
		String timeml;

		if (!htWrappers.containsKey(quote.getLanguage()))
			htWrappers.put(quote.getLanguage(), new HeidelTimeWrapper(getLanguage(quote.getLanguage())));

		HeidelTimeWrapper ht = htWrappers.get(quote.getLanguage());

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

	public String getTime(QuoteLocale quote) {
		String timeml;

		if (!htWrappers.containsKey(quote.getLanguage()))
			htWrappers.put(quote.getLanguage(), new HeidelTimeWrapper(getLanguage(quote.getLanguage())));

		HeidelTimeWrapper ht = htWrappers.get(quote.getLanguage());

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

		return getDayTime(toReturn);
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

}
