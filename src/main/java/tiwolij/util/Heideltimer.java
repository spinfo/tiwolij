package tiwolij.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import tiwolij.domain.Quote;

public class Heideltimer {

	private Map<String, String> dayTimes = new HashMap<String, String>();

	private Map<String, HeideltimeWrapper> htWrappers = new HashMap<String, HeideltimeWrapper>();

	public Heideltimer() {
		dayTimes.put("MO", "08:00");
		dayTimes.put("EV", "20:00");
		dayTimes.put("AF", "16:00");
		dayTimes.put("NI", "23:00");
		dayTimes.put("DT", "12:00");
	}

	public String getDayTime(String time) {
		if (dayTimes.containsKey(time)) {
			return dayTimes.get(time);
		}

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

	public String getTime(Quote quote) {
		String timeml;

		if (!htWrappers.containsKey(quote.getLanguage())) {
			htWrappers.put(quote.getLanguage(), new HeideltimeWrapper(getLanguage(quote.getLanguage())));
		}

		HeideltimeWrapper ht = htWrappers.get(quote.getLanguage());

		try {
			timeml = ht.process(quote.getCorpus());
		} catch (DocumentCreationTimeMissingException e) {
			e.printStackTrace();
			return null;
		}

		String[] split = quote.getSchedule().split("-");
		Integer day = Integer.parseInt(split[0]);
		Integer month = Integer.parseInt(split[1]);
		String toReturn = parseTime(timeml, day, month);

		return getDayTime(toReturn);
	}

	public String getYear(Quote quote) {
		String timeml;

		if (!htWrappers.containsKey(quote.getLanguage())) {
			htWrappers.put(quote.getLanguage(), new HeideltimeWrapper(getLanguage(quote.getLanguage())));
		}

		HeideltimeWrapper ht = htWrappers.get(quote.getLanguage());

		try {
			timeml = ht.process(quote.getCorpus());
		} catch (DocumentCreationTimeMissingException e) {
			e.printStackTrace();
			return null;
		}

		String[] split = quote.getSchedule().split("-");
		Integer day = Integer.parseInt(split[0]);
		Integer month = Integer.parseInt(split[1]);

		return parseYear(timeml, day, month);
	}

	private String parseTime(String string, Integer day, Integer month) {
		String regex = "type=\"TIME\" value=\"[0-9|X]{4}-month-dayT([0-9|A-Z|:]+)\">";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);

		if (matcher.find()) {
			return matcher.group(1);
		}

		regex = "type=\"TIME\" value=\"[0-9|X]{4}-[0-9|X]{2}-[0-9|X]{2}T([0-9|A-Z|:]+)\">";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(string);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null;
	}

	private String parseYear(String string, Integer day, Integer month) {
		String regex = "type=\"(DATE|TIME)\" value=\"([0-9]{4})-" + month + "-" + day;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);

		if (matcher.find()) {
			return matcher.group(2);
		}

		regex = "type=\"(DATE|TIME)\" value=\"([0-9]{4})-";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(string);

		if (matcher.find()) {
			return matcher.group(2);
		}

		return null;
	}

}
