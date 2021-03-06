package tiwolij.util;

import java.text.Normalizer;

public class StringEncoding {

	public static String toSlug(String str) {
		str = str.toLowerCase().replace("ü", "ue").replace("ö", "oe").replace("ä", "ae").replace("ß", "ss");
		str = (str.contains("_")) ? str.replace("_", " ") : str;
		str = str.replaceAll("[\\p{Punct}]", "").replaceAll("\\s", "_");
		str = Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\x00-\\x7F]", "");

		return str;
	}

}
