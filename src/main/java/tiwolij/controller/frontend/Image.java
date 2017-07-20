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
			return;
		}

		Author author = authors.getOneById(authorId);

		if (author == null || !author.hasImage()) {
			response.sendRedirect("/img/tiwoli.png");
			return;
		}

		response.setContentType("image/jpeg");
		response.getOutputStream().write(author.getImage());
		response.getOutputStream().close();
	}

	@GetMapping("/flashcard")
	public void flashcard(HttpServletResponse response, @RequestParam(name = "id") Integer quoteId,
			@RequestParam(name = "onlytext", defaultValue = "0") Boolean onlytext) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BufferedImage image = new BufferedImage(900, 600, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphic = image.createGraphics();
		graphic.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		Quote quote = quotes.getOneById(quoteId);
		String language = quote.getLanguage();
		Locale work = quote.getWork().getMappedLocales().get(language);
		Locale author = quote.getAuthor().getMappedLocales().get(language);
		onlytext = (!author.getAuthor().hasImage()) ? true : onlytext;

		java.util.Locale locale = new java.util.Locale(language);
		String day = messages.getMessage("day." + quote.getDay(), null, locale);
		String delim = messages.getMessage("months.delim", null, locale);
		String month = messages.getMessage("months." + quote.getMonth(), null, locale);
		String schedule = day + delim + month;

		// colors
		Color text = new Color(51, 51, 51);
		Color textmuted = new Color(119, 119, 119);

		// background image
		BufferedImage bgimg = ImageIO.read(getClass().getClassLoader().getResource("static/img/card.png"));
		graphic.drawImage(bgimg, 0, 0, null);

		// formated texts
		Float x, y, bound;
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
		y = 50f;
		bound = 865f;

		// measure.getPosition();
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

			x = 330f;
			bound = 550f;
			while (measure.getPosition() < iter.getEndIndex()) {
				TextLayout layout = measure.nextLayout(bound);
				y += layout.getAscent();
				layout.draw(graphic, x, y);
				y += layout.getDescent() + layout.getLeading();
			}
		}

		// image
		BufferedImage img = onlytext ? ImageIO.read(getClass().getClassLoader().getResource("static/img/tiwoli.png"))
				: ImageIO.read(new ByteArrayInputStream(author.getAuthor().getImage()));
		Integer imgWidth = img.getWidth();
		Integer imgHeight = img.getHeight();

		if (imgWidth > 280) {
			imgWidth = 280;
			imgHeight = (imgWidth * img.getHeight()) / img.getWidth();
		}
		if (imgHeight > 400) {
			imgHeight = 400;
			imgWidth = (imgHeight * img.getWidth()) / img.getHeight();
		}

		Integer center = 25 + (280 - imgWidth) / 2;
		graphic.drawImage(img.getScaledInstance(imgWidth, imgHeight, java.awt.Image.SCALE_SMOOTH), center, 85, null);

		if (!onlytext) {

			// image details
			String name = messages.getMessage("fields.image", null, locale) + ": " + author.getName();

			graphic.setColor(text);
			attstr = new AttributedString(name);
			attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.BOLD, 15));
			iter = attstr.getIterator();
			measure = new LineBreakMeasurer(iter, frc);
			measure.setPosition(iter.getBeginIndex());

			x = 35f;
			y = 565f;
			bound = 865f;
			measure.nextLayout(bound).draw(graphic, x, y);

			// graphic.setColor(text);
			// attstr = new AttributedString(name);
			// attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF,
			// Font.PLAIN, 15));
			// iter = attstr.getIterator();
			// measure = new LineBreakMeasurer(iter, frc);
			// measure.setPosition(iter.getBeginIndex());
			//
			// x = 35f;
			// y = 90f + imgHeight;
			// bound = 290f;
			// while (measure.getPosition() < iter.getEndIndex()) {
			// TextLayout layout = measure.nextLayout(bound);
			// dx = x + (bound - layout.getAdvance()) / 2;
			// y += layout.getAscent();
			// layout.draw(graphic, dx, y);
			// y += layout.getDescent() + layout.getLeading();
			// }

			// imageAttribution
			String attribution = quote.getWork().getAuthor().getImageAttribution();
			attribution = messages.getMessage("fields.imageAttribution", null, locale) + ": " + attribution;

			graphic.setColor(textmuted);
			attstr = new AttributedString(attribution);
			attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.PLAIN, 11));

			iter = attstr.getIterator();
			measure = new LineBreakMeasurer(iter, frc);
			measure.setPosition(iter.getBeginIndex());

			// x = 35f;
			// y = 565f;
			// bound = 830f;
			x = 865f - measure.nextLayout(bound).getAdvance();
			measure.setPosition(iter.getBeginIndex());
			measure.nextLayout(bound).draw(graphic, x, y);

			// x = 25f;
			// y = 510f;
			// bound = 290f;
			// while (measure.getPosition() < iter.getEndIndex()) {
			// TextLayout layout = measure.nextLayout(bound);
			// dx = x + (bound - layout.getAdvance()) / 2;
			// y += layout.getAscent();
			// layout.draw(graphic, dx, y);
			// y += layout.getDescent() + layout.getLeading();
			// }
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
