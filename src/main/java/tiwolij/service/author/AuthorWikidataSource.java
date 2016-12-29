package tiwolij.service.author;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;
import tiwolij.service.wikidata.WikidataService;

@Component
public class AuthorWikidataSource {

	@Autowired
	private Environment env;

	@Autowired
	private WikidataService wikidata;

	public Author byUrl(URL url) {
		Author author = null;
		Integer wikidataId = wikidata.getWikidataId(url);

		if (wikidataId != null) {
			author = byWikidataId(wikidataId);
		}

		return author;
	}

	public Author byWikidataId(Integer wikidataId) {
		Author author = new Author();

		author.setWikidataId(wikidataId);
		author.setSlug(wikidata.getSlug(wikidataId));
		author.setImageAttribution(wikidata.getImageAttribution(wikidataId));
		image(author, wikidata.getImage(wikidataId));

		return author;
	}

	public Author image(Author author, URL url) {
		Integer height = Integer.parseInt(env.getProperty("tiwolij.import.imageheight"));

		try {
			byte[] bytes = IOUtils.toByteArray(url.openStream());
			BufferedImage input = ImageIO.read(new ByteArrayInputStream(bytes));
			Integer width = (height * input.getWidth()) / input.getHeight();
			Image output = input.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(output, 0, 0, new Color(0, 0, 0), null);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, "jpg", stream);
			author.setImage(stream.toByteArray());
		} catch (Exception e) {
		}

		return author;
	}

	public AuthorLocale locale(Integer wikidataId, String language) {
		return wikidata.getAuthorLocale(wikidataId, language);
	}

}
