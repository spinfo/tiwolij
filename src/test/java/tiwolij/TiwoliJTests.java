package tiwolij;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;

/**
 * Smoke test: renders every page against an in-memory database, so template
 * and mapping errors surface without a MySQL instance.
 */
@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:tiwolij;MODE=MySQL;DATABASE_TO_LOWER=TRUE;NON_KEYWORDS=YEAR;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TiwoliJTests {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private AuthorService authors;

	@Autowired
	private QuoteService quotes;

	private MockMvc mvc;

	private Author author;

	@BeforeAll
	void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();

		Work work = new Work().setSlug("Faust");
		work.addLocale(new Locale().setLanguage("en").setName("Faust"));
		work.addQuote(new Quote().setSchedule("24-10").setLanguage("en")
				.setCorpus("It was the 24th of October 1808 at 8 o'clock.<br/>A second line."));

		author = new Author().setSlug("Johann_Wolfgang_von_Goethe");
		author.addLocale(new Locale().setLanguage("en").setName("Johann Wolfgang von Goethe"));
		author.addWork(work);
		author = authors.save(author);
	}

	private Integer quoteId() {
		return quotes.getAll().get(0).getId();
	}

	private Integer workId() {
		return author.getWorks().get(0).getId();
	}

	private Integer localeId() {
		return author.getLocales().get(0).getId();
	}

	private String render(String url) throws Exception {
		String html = mvc.perform(get(url)).andExpect(status().isOk()).andReturn().getResponse()
				.getContentAsString();

		// Thymeleaf 3 keeps self-closing tags, which browsers read as open tags
		assertThat(html).doesNotContainPattern("<(script|div|span|a|td|th|p|textarea|button|option|title)\\b[^>]*/>");
		return html;
	}

	@ParameterizedTest
	@ValueSource(strings = { "/home", "/about", "/list?schedule=24-10&lang=en", "/view?schedule=24-10&lang=en",
			"/view?schedule=01-01&lang=en", "/tiwolij/", "/tiwolij/search?term=goethe",
			"/tiwolij/authors/list?page=0&sort=id", "/tiwolij/works/list?page=0&sort=id",
			"/tiwolij/quotes/list?page=0&sort=id", "/tiwolij/locales/list?page=0&sort=id", "/tiwolij/authors/create",
			"/tiwolij/works/create", "/tiwolij/quotes/create", "/tiwolij/locales/create", "/tiwolij/data/import",
			"/tiwolij/data/import/json", "/tiwolij/data/import/tsv", "/tiwolij/data/export",
			"/tiwolij/data/export/json", "/tiwolij/data/export/posts", "/tiwolij/data/export/tweets",
			"/tiwolij/data/heideltag" })
	void rendersPage(String url) throws Exception {
		render(url);
	}

	@Test
	void rendersEntityPages() throws Exception {
		assertThat(render("/view?id=" + quoteId() + "&lang=en")).contains("A second line");
		assertThat(render("/tiwolij/authors/view?authorId=" + author.getId())).contains("johann_wolfgang_von_goethe");
		render("/tiwolij/authors/edit?authorId=" + author.getId());
		render("/tiwolij/works/view?workId=" + workId());
		render("/tiwolij/works/edit?workId=" + workId());
		render("/tiwolij/quotes/view?quoteId=" + quoteId());
		render("/tiwolij/quotes/edit?quoteId=" + quoteId());
		render("/tiwolij/locales/view?localeId=" + localeId());
		render("/tiwolij/locales/edit?localeId=" + localeId());
	}

	@Test
	void navbarAndPagerUseRequestInfo() throws Exception {
		String html = render("/tiwolij/authors/list?page=0&sort=id");
		assertThat(html).contains("class=\"dropdown active\"");
		assertThat(html).contains("?page=0&amp;sort=id");
	}

	@Test
	void rendersFlashcards() throws Exception {
		for (String url : new String[] { "/image/flashcard?id=", "/image/textcard?id=" }) {
			byte[] png = mvc.perform(get(url + quoteId())).andExpect(status().isOk()).andReturn().getResponse()
					.getContentAsByteArray();
			assertThat(png).startsWith(0x89, 'P', 'N', 'G');
		}
	}

	@Test
	void exportsAndReimportsJson() throws Exception {
		byte[] json = mvc.perform(post("/tiwolij/data/export/json")).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsByteArray();
		assertThat(new String(json, "UTF-8")).contains("24th of October 1808");

		long before = quotes.count();
		MockHttpSession session = new MockHttpSession();
		mvc.perform(multipart("/tiwolij/data/import/json").file(new MockMultipartFile("file", json)).session(session))
				.andExpect(status().is3xxRedirection());
		mvc.perform(get("/tiwolij/data/import/process").session(session)).andExpect(status().is3xxRedirection());
		mvc.perform(get("/tiwolij/data/import/report").session(session)).andExpect(status().isOk());

		assertThat(quotes.count()).isEqualTo(before + 1);
	}

	@Test
	void exportsMastodonPosts() throws Exception {
		String tsv = mvc.perform(post("/tiwolij/data/export/posts")).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();
		assertThat(tsv).contains("https://tiwoli.spinfo.uni-koeln.de/image/flashcard?id=");
	}

}
