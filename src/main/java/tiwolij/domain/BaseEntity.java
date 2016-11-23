package tiwolij.domain;

public class BaseEntity {

	public Object get(String field) {
		Object result = null;

		try {
			result = getClass().getDeclaredField(field).get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void set(String field, String value) {
		try {
			getClass().getDeclaredField(field).set(this, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int compareNaturalBy(BaseEntity that, String by) {
		String s1 = this.get(by) == null ? "" : this.get(by).toString();
		String s2 = that.get(by) == null ? "" : that.get(by).toString();

		// http://stackoverflow.com/a/27530518
		int len1 = s1.length();
		int len2 = s2.length();

		int i;
		char c1, c2;

		for (i = 0, c1 = 0, c2 = 0; (i < len1) && (i < len2) && (c1 = s1.charAt(i)) == (c2 = s2.charAt(i)); i++)
			continue;

		// Check end of string
		if (c1 == c2)
			return (len1 - len2);

		// Check digit in first string
		if (Character.isDigit(c1)) {

			// Check digit only in first string
			if (!Character.isDigit(c2))
				return ((i > 0) && Character.isDigit(s1.charAt(i - 1)) ? 1 : c1 - c2);

			// Scan all integer digits
			int x1, x2;

			for (x1 = i + 1; (x1 < len1) && Character.isDigit(s1.charAt(x1)); x1++)
				continue;

			for (x2 = i + 1; (x2 < len2) && Character.isDigit(s2.charAt(x2)); x2++)
				continue;

			// Longer integer wins, first digit otherwise
			return (x2 == x1 ? c1 - c2 : x1 - x2);
		}

		// Check digit only in second string
		if (Character.isDigit(c2))
			return ((i > 0) && Character.isDigit(s2.charAt(i - 1)) ? -1 : c1 - c2);

		// No digits
		return (c1 - c2);
	}

}
