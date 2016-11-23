package tiwolij.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unihd.dbs.heideltime.standalone.exceptions.DocumentCreationTimeMissingException;
import de.unihd.dbs.uima.annotator.heideltime.resources.Language;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.Work;

public class tivoliChirp {

//	private Map<String, String> dayTimes = new HashMap<String, String>();
	
//	private Map<String, String> times = new HashMap<String, String>();
//	private Map<Integer, String> years = new HashMap<Integer, String>();

//	public DateAndTime() {
//		dayTimes.put("MO", "08:00");
//		dayTimes.put("EV", "20:00");
//		dayTimes.put("AF", "16:00");
//		dayTimes.put("NI", "23:00");
//		dayTimes.put("DT", "12:00");
//	}

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

	public String getTime(QuoteLocale quote, HeidelTimeWrapper ht)  {
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
			//times.put(matcher.group(1), string);
			System.out.println(matcher.group(1));
			return matcher.group(1);
		}
		regex = "type=\"TIME\" value=\"[0-9|X]{4}-[0-9|X]{2}-[0-9|X]{2}T([0-9|A-Z|:]+)\">";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(string);
		if (matcher.find()) {
			//times.put(matcher.group(1),string);
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
			//years.put(Integer.parseInt(matcher.group(2)), string);
			System.out.println(matcher.group(2));
			return matcher.group(2);
		}
		regex = "type=\"(DATE|TIME)\" value=\"([0-9]{4})-";
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(string);
		if (matcher.find()) {
			//years.put(Integer.parseInt(matcher.group(2)), string);
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
	
	
}
