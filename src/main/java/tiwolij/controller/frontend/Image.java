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
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import tiwolij.domain.AuthorLocale;
import tiwolij.domain.QuoteLocale;
import tiwolij.domain.WorkLocale;
import tiwolij.service.author.AuthorService;
import tiwolij.service.quote.QuoteService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/image")
public class Image {

	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@Autowired
	private QuoteService quotes;

	@Autowired
	private MessageSource messages;

	@GetMapping("/author")
	public void author(@RequestParam(name = "id") Integer authorId, HttpServletResponse response) throws Exception {
		if (authors.getAuthor(authorId) != null && authors.getAuthor(authorId).getImage() != null) {
			response.setContentType("image/jpeg, image/jpg, image/png, image/gif");
			response.getOutputStream().write(authors.getAuthor(authorId).getImage());
			response.getOutputStream().close();
		} else
			response.sendRedirect("/img/tiwoli.png");
	}

	@GetMapping("/flashcard")
	public void flashcard(@RequestParam(name = "id") Integer quoteId, @RequestParam(name = "lang") String language,
			HttpServletResponse response) throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		BufferedImage image = new BufferedImage(900, 600, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphic = image.createGraphics();
		graphic.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		QuoteLocale quote = quotes.getLocaleByQuoteAndLang(quoteId, language);
		WorkLocale work = works.getLocaleByLang(quote.getQuote().getWork().getId(), language);
		AuthorLocale author = authors.getLocaleByLang(work.getWork().getAuthor().getId(), language);

		Locale locale = new Locale(language);
		String month = messages.getMessage("months." + quote.getSchedule().split("-")[1], null, locale);
		String schedule = quote.getSchedule().split("-")[0] + messages.getMessage("months.delim", null, locale) + month;

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

		// header
		graphic.setColor(text);
		graphic.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		graphic.drawString(schedule + " - " + author.getName() + ": " + work.getName(), 35, 68);

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

		// formated texts
		Float dx, x, y, bound;
		AttributedString attstr;
		LineBreakMeasurer measure;
		AttributedCharacterIterator iter;
		FontRenderContext frc = graphic.getFontRenderContext();

		// corpus
		String corpus = quote.getCorpus().replaceAll("<br[^>]*>", " ").replaceAll("<[^>]*>", "");
		
		graphic.setColor(text);
		attstr = new AttributedString(corpus);
		attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		iter = attstr.getIterator();
		measure = new LineBreakMeasurer(iter, frc);
		measure.setPosition(iter.getBeginIndex());

		x = 360f;
		y = 120f;
		bound = 510f;
		while (measure.getPosition() < iter.getEndIndex()) {
			TextLayout layout = measure.nextLayout(bound);
			y += layout.getAscent();
			layout.draw(graphic, x, y);
			y += layout.getDescent() + layout.getLeading();
		}

		// author name
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
		String attribution = quote.getQuote().getWork().getAuthor().getImageAttribution();
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

		// done
		ImageIO.write(image, "png", stream);
		response.getOutputStream().write(stream.toByteArray());
		response.getOutputStream().close();
	}

}
