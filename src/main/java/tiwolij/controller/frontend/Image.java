package tiwolij.controller.frontend;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tiwolij.domain.Author;
import tiwolij.domain.Locale;
import tiwolij.domain.Quote;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;

@Controller
@RequestMapping("/image")
public class Image {

	@Autowired
	private MessageSource messages;

	@Autowired
	private AuthorService authors;

	@Autowired
	private QuoteService quotes;

	@GetMapping("/author")
	public void author(@RequestParam(name = "id") Integer authorId, HttpServletResponse response) throws Exception {
		if (!authors.existsById(authorId)) {
			response.sendRedirect("/img/tiwoli.png");
		}

		Author author = authors.getOneById(authorId);

		if (author == null || !author.hasImage()) {
			response.sendRedirect("/img/tiwoli.png");
		}

		response.setContentType("image/jpeg");
		response.getOutputStream().write(author.getImage());
		response.getOutputStream().close();
	}

	@GetMapping("/flashcard")
	public void flashcard(HttpServletResponse response, @RequestParam(name = "id") Integer quoteId,
			@RequestParam(name = "onlytext", defaultValue = "0") Boolean onlytext) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BufferedImage image = new BufferedImage(900, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphic = image.createGraphics();
		graphic.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		Quote quote = quotes.getOneById(quoteId);
		String language = quote.getLanguage();
		Locale work = quote.getWork().getMappedLocales().get(language);
		Locale author = quote.getAuthor().getMappedLocales().get(language);
		onlytext = (!onlytext) ? !author.getAuthor().hasImage() : false;

		java.util.Locale locale = new java.util.Locale(language);
		String day = messages.getMessage("day." + quote.getDay(), null, locale);
		String delim = messages.getMessage("months.delim", null, locale);
		String month = messages.getMessage("months." + quote.getMonth(), null, locale);
		String schedule = day + delim + month;

		// colors
		Color text = new Color(51, 51, 51);
		Color textmuted = new Color(119, 119, 119);
		Color lightgrey = new Color(211, 211, 211);
		Color darkgrey = new Color(169, 169, 169);

		// background
		graphic.setColor(lightgrey);
		graphic.fillRect(0, 0, image.getWidth(), image.getHeight());

		// header box
		graphic.setColor(darkgrey);
		graphic.fillRect(30, 30, 840, 60);

		// formated texts
		Float dx, x, y, bound;
		AttributedString attstr;
		LineBreakMeasurer measure;
		AttributedCharacterIterator iter;
		FontRenderContext frc = graphic.getFontRenderContext();

		// header
		String header = schedule + " - " + author.getName() + ": " + work.getName();

		graphic.setColor(text);
		attstr = new AttributedString(header);
		attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.BOLD, 20));
		iter = attstr.getIterator();
		measure = new LineBreakMeasurer(iter, frc);
		measure.setPosition(iter.getBeginIndex());

		x = 35f;
		y = 68f;
		bound = 830f;

		measure.getPosition();
		measure.nextLayout(bound).draw(graphic, x, y);

		// corpus
		String corpus[] = quote.getCorpus().split("<br[^>]*>");

		y = 120f;
		for (String i : corpus) {
			graphic.setColor(text);
			attstr = new AttributedString(i.replaceAll("<[^>]*>", ""));
			attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
			iter = attstr.getIterator();
			measure = new LineBreakMeasurer(iter, frc);
			measure.setPosition(iter.getBeginIndex());

			x = (onlytext) ? 30 : 360f;
			bound = (onlytext) ? 840 : 510f;
			while (measure.getPosition() < iter.getEndIndex()) {
				TextLayout layout = measure.nextLayout(bound);
				y += layout.getAscent();
				layout.draw(graphic, x, y);
				y += layout.getDescent() + layout.getLeading();
			}
		}

		// image
		if (!onlytext) {

			// image box
			graphic.setColor(darkgrey);
			graphic.fillRect(30, 120, 300, 450);

			// image
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(author.getAuthor().getImage()));
			Integer width = img.getWidth();
			Integer height = img.getHeight();

			if (width > 290) {
				width = 290;
				height = (width * img.getHeight()) / img.getWidth();
			}
			if (height > 420) {
				height = 420;
				width = (height * img.getWidth()) / img.getHeight();
			}

			Integer center = 35 + (290 - width) / 2;
			graphic.drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), center, 125, null);

			// image details
			String name = author.getName();

			graphic.setColor(text);
			attstr = new AttributedString(name);
			attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.PLAIN, 16));
			iter = attstr.getIterator();
			measure = new LineBreakMeasurer(iter, frc);
			measure.setPosition(iter.getBeginIndex());

			x = 35f;
			y = 130f + height;
			bound = 290f;
			while (measure.getPosition() < iter.getEndIndex()) {
				TextLayout layout = measure.nextLayout(bound);
				dx = x + (bound - layout.getAdvance()) / 2;
				y += layout.getAscent();
				layout.draw(graphic, dx, y);
				y += layout.getDescent() + layout.getLeading();
			}

			// imageAttribution
			String attribution = quote.getWork().getAuthor().getImageAttribution();
			attribution = messages.getMessage("fields.imageAttribution", null, locale) + ": " + attribution;

			graphic.setColor(textmuted);
			attstr = new AttributedString(attribution);
			attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.PLAIN, 14));
			iter = attstr.getIterator();
			measure = new LineBreakMeasurer(iter, frc);
			measure.setPosition(iter.getBeginIndex());

			x = 35f;
			y = 570f;
			bound = 290f;
			while (measure.getPosition() < iter.getEndIndex()) {
				TextLayout layout = measure.nextLayout(bound);
				dx = x + (bound - layout.getAdvance()) / 2;
				y += layout.getAscent();
				layout.draw(graphic, dx, y);
				y += layout.getDescent() + layout.getLeading();
			}
		}

		// done
		ImageIO.write(image, "png", stream);
		response.setContentType("image/png");
		response.getOutputStream().write(stream.toByteArray());
		response.getOutputStream().close();
	}

	@GetMapping("/textcard")
	public void textcard(HttpServletResponse response, @RequestParam(name = "id") Integer quoteId) throws Exception {
		flashcard(response, quoteId, true);
	}

}
