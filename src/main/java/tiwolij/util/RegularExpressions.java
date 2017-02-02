package tiwolij.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressions {

	private Pattern date = Pattern
			.compile("(?<day>3[0-1]|[1-2][0-9]|0?[1-9])[.-](?<month>1[0-2]|0?[1-9])([.-](?<year>\\d+))?");

	private Pattern time = Pattern
			.compile("(?<hours>2[0-3]|[0-1]?[0-9]):(?<minutes>[0-5][0-9])(:(?<seconds>[0-5][0-9]))?");

	private Pattern wikidataId = Pattern.compile("Q(?<wikidataId>\\d+)");

	private Pattern wikipediaLang = Pattern.compile("(?<lang>[a-z]{2})\\.wikipedia\\.org");

	private Pattern wikipediaSlug = Pattern.compile("wiki/(?<slug>[^/]*)$");

	public String datetime(String haystack) {
		String result = null;
		String date = date(haystack);
		String time = time(haystack);

		if (date != null) {
			result = date;

			if (time != null) {
				result += " " + time;
			}
		}

		return result;
	}

	public String date(String haystack) {
		Matcher needle = date.matcher(haystack);
		String result = null;

		if (needle.find()) {
			String day = null;
			String month = null;
			String year = null;

			if (needle.group("day") != null && !needle.group("day").isEmpty()) {
				day = String.format("%02d", Integer.parseInt(needle.group("day")));
			}

			if (needle.group("month") != null && !needle.group("month").isEmpty()) {
				month = String.format("%02d", Integer.parseInt(needle.group("month")));
			}

			if (needle.group("year") != null && !needle.group("year").isEmpty()) {
				year = String.format("%04d", Integer.parseInt(needle.group("year")));
			}

			if (day != null && month != null) {
				result = day + "-" + month;

				if (year != null) {
					result += "-" + year;
				}
			}
		}

		return result;
	}

	public String time(String haystack) {
		Matcher needle = time.matcher(haystack);
		String result = null;

		if (needle.find()) {
			String hours = null;
			String minutes = null;
			String seconds = null;

			if (needle.group("hours") != null && !needle.group("hours").isEmpty()) {
				hours = needle.group("hours");
			}

			if (needle.group("minutes") != null && !needle.group("minutes").isEmpty()) {
				minutes = needle.group("minutes");
			}

			if (needle.group("seconds") != null && !needle.group("seconds").isEmpty()) {
				seconds = needle.group("seconds");
			}

			if (hours != null && minutes != null) {
				result = hours + ":" + minutes;

				if (seconds != null) {
					result += ":" + seconds;
				} else {
					result += ":00";
				}
			}

		}

		return result;
	}

	public Integer wikidataId(String haystack) {
		Matcher needle = wikidataId.matcher(haystack);
		Integer result = null;

		if (needle.find()) {
			if (needle.group("wikidataId") != null && !needle.group("wikidataId").isEmpty()) {
				try {
					result = Integer.parseInt(needle.group("wikidataId"));
				} catch (Exception NumberFormatException) {
				}
			}
		}

		return result;
	}

	public String wikipediaLang(String haystack) {
		Matcher needle = wikipediaLang.matcher(haystack.toLowerCase());
		String result = null;

		if (needle.find()) {
			if (needle.group("lang") != null && !needle.group("lang").isEmpty()) {
				result = needle.group("lang");
			}
		}

		return result;
	}

	public String wikipediaSlug(String haystack) {
		Matcher needle = wikipediaSlug.matcher(haystack);
		String result = null;

		if (needle.find()) {
			if (needle.group("slug") != null && !needle.group("slug").isEmpty()) {
				result = needle.group("slug");
			}
		}

		return result;
	}

}
