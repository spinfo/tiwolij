package tiwolij.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import tiwolij.domain.Quote;

public class HeideltimerTests {

	@Test
	public void testGetYear() {
		Quote quote = new Quote().setSchedule("05-05").setLanguage("en")
				.setCorpus("On the 5th of May 1821 Napoleon died on Saint Helena.");

		assertThat(new Heideltimer().getYear(quote)).isEqualTo("1821");
	}

	@Test
	public void testGetYearGerman() {
		Quote quote = new Quote().setSchedule("24-10").setLanguage("de")
				.setCorpus("Am 24. Oktober 1808 traf er um acht Uhr abends ein.");

		assertThat(new Heideltimer().getYear(quote)).isEqualTo("1808");
	}

}
