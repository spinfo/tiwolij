package tiwolij.service.wikidata;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.MonolingualTextValue;
import org.wikidata.wdtk.datamodel.interfaces.Value;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.domain.Work;
import tiwolij.util.ImageLoader;
import tiwolij.util.RegularExpressions;

@Component
public class WikidataRepository {

	@Autowired
	private Environment env;

	private RegularExpressions regex = new RegularExpressions();

	public Author getAuthor(Author author) {
		try {
			if (!author.hasWikidataId() && author.hasWorks()) {
				for (Work i : author.getWorks()) {
					if (i.hasWikidataId()) {
						author.setWikidataId(extractAuthor(i.getWikidataId()));
					}
					if (author.hasWikidataId()) {
						break;
					}
				}
			}

			if (!author.hasWikidataId() && author.hasLocales()) {
				for (Locale i : author.getLocales()) {
					if (i.hasHref()) {
						author.setWikidataId(extractWikidataId(new URL(i.getHref())));
					}
					if (author.hasWikidataId()) {
						break;
					}
				}
			}
		} catch (Exception e) {
		}

		if (author.hasWikidataId()) {
			Integer height = env.getProperty("tiwolij.import.imageheight", Integer.class);

			String slug = extractSlug(author.getWikidataId());
			byte[] image = ImageLoader.getBytes(extractImage(author.getWikidataId()), height);
			String imageAttribution = extractImageAttribution(author.getWikidataId());

			author.setSlug((slug != null) ? slug : author.getSlug());
			author.setImage((image != null) ? image : author.getImage());
			author.setImageAttribution((imageAttribution != null) ? imageAttribution : author.getImageAttribution());

			if (author.hasLocales()) {
				List<Locale> locales = author.getLocales();
				author.setLocales(new ArrayList<Locale>());

				for (Locale i : locales) {
					author.addLocale(extractLocale(author.getWikidataId(), i.getLanguage()));
				}
			}
		}

		return author;
	}

	public Work getWork(Work work) {
		try {
			if (!work.hasWikidataId() && work.hasLocales()) {
				for (Locale i : work.getLocales()) {
					if (i.hasHref()) {
						work.setWikidataId(extractWikidataId(new URL(i.getHref())));
					}
					if (work.hasWikidataId()) {
						break;
					}
				}
			}
		} catch (Exception e) {
		}

		if (work.hasWikidataId()) {
			String slug = extractSlug(work.getWikidataId());

			work.setSlug((slug != null) ? slug : work.getSlug());

			if (work.hasLocales()) {
				List<Locale> locales = work.getLocales();
				work.setLocales(new ArrayList<Locale>());

				for (Locale i : locales) {
					work.addLocale(extractLocale(work.getWikidataId(), i.getLanguage()));
				}
			}
		}

		return work;
	}

	public Quote getQuote(Quote quote) {
		getWork(quote.getWork());
		getAuthor(quote.getAuthor());
		return quote;
	}

	public Locale extractLocale(Integer wikidataId, String language) {
		Locale locale = null;

		try {
			WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
			ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);
			MonolingualTextValue label = null;

			if (item.getLabels().containsKey(language)) {
				locale = new Locale();
				label = item.getLabels().get(language);
				URL url = new URL("https://" + language + ".wikipedia.org/wiki/" + label.getText().replace(" ", "_"));

				locale.setLanguage(language);
				locale.setName(label.getText());
				locale.setHref(url.toString());
			}
		} catch (Exception e) {
		}

		return locale;
	}

	public Integer extractWikidataId(URL url) {
		Integer wikidataId = null;
		String title = regex.wikipediaSlug(url.getPath());
		String language = regex.wikipediaLang(url.getHost());
		String wikimediaPageprops = env.getProperty("wikimedia.pageprops");

		if (title != null && language != null) {
			try {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = builder
						.parse(new URL("https://" + language + wikimediaPageprops + title).openStream());
				Node node = document.getElementsByTagName("pageprops").item(0);

				if (node != null) {
					String value = node.getAttributes().getNamedItem("wikibase_item").getNodeValue();
					wikidataId = regex.wikidataId(value);
				}
			} catch (Exception e) {
			}
		}

		return wikidataId;
	}

	public Integer extractAuthor(Integer wikidataId) {
		Integer authorId = null;

		try {
			WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
			ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

			if (item.hasStatement("P50")) {
				Value value = item.findStatementGroup("P50").getStatements().get(0).getValue();
				authorId = regex.wikidataId(value.toString());
			}
		} catch (Exception e) {
		}

		return authorId;
	}

	public String extractSlug(Integer wikidataId) {
		String slug = null;
		String defaultLanguage = env.getProperty("tiwolij.locales.default");
		String[] allowedLanguages = env.getProperty("tiwolij.locales.allowed", String[].class);

		try {
			WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
			ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);
			MonolingualTextValue label = null;

			if (item.getLabels().containsKey(defaultLanguage)) {
				label = item.getLabels().get(defaultLanguage);
			} else {
				for (String i : allowedLanguages) {
					if (item.getLabels().containsKey(i)) {
						label = item.getLabels().get(i);
						break;
					}
				}
			}

			if (label != null) {
				slug = label.getText().replaceAll(" ", "_");
			}
		} catch (Exception e) {
		}

		return slug;
	}

	public URL extractImage(Integer wikidataId) {
		URL url = null;
		String wikimediaCommons = env.getProperty("wikimedia.commons");

		try {
			WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
			ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

			if (item.hasStatement("P18")) {
				Value value = item.findStatementGroup("P18").getStatements().get(0).getValue();
				String file = value.toString().replaceAll("^\"|\"$", "").replace(" ", "_");
				String md5 = DigestUtils.md5Hex(file);
				String dir = md5.substring(0, 1) + "/" + md5.substring(0, 2) + "/";

				url = new URL("https://" + wikimediaCommons + dir + file);
			}
		} catch (Exception e) {
		}

		return url;
	}

	public String extractImageAttribution(Integer wikidataId) {
		String imageAttribution = null;
		String wikimediaImageinfo = env.getProperty("wikimedia.imageinfo");

		String artist = "Unknown Artist";
		String license = "Unspecified License";

		try {
			WikibaseDataFetcher data = WikibaseDataFetcher.getWikidataDataFetcher();
			ItemDocument item = (ItemDocument) data.getEntityDocument("Q" + wikidataId);

			if (item.hasStatement("P18")) {
				Value value = item.findStatementGroup("P18").getStatements().get(0).getValue();
				String file = value.toString().replaceAll("^\"|\"$", "").replace(" ", "_");

				URL url = new URL("https://" + wikimediaImageinfo + file);
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document document = builder.parse(url.openStream());

				Node artistNode = document.getElementsByTagName("Artist").item(0);
				Node licenseNode = document.getElementsByTagName("LicenseShortName").item(0);

				if (artistNode != null) {
					artist = artistNode.getAttributes().getNamedItem("value").getNodeValue();
					artist = StringUtils.abbreviate(artist.replaceAll("\\<[^>]*>", ""), 100);
				}

				if (licenseNode != null) {
					license = licenseNode.getAttributes().getNamedItem("value").getNodeValue();
					license = StringUtils.abbreviate(license.replaceAll("\\<[^>]*>", ""), 100);
				}

				imageAttribution = StringEscapeUtils.unescapeHtml4(artist) + " ("
						+ StringEscapeUtils.unescapeHtml4(license) + ")";
			}
		} catch (Exception e) {
		}

		return imageAttribution;
	}

}
