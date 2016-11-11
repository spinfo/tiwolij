package tiwolij.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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

		QuoteLocale quote = quotes.getLocaleByQuoteAndLang(quoteId, language);
		WorkLocale work = works.getLocaleByLang(quote.getQuote().getWork().getId(), language);
		AuthorLocale author = authors.getLocaleByLang(work.getWork().getAuthor().getId(), language);

		// background
		graphic.setColor(Color.LIGHT_GRAY);
		graphic.fillRect(0, 0, image.getWidth(), image.getHeight());

		// header box
		graphic.setColor(Color.GRAY);
		graphic.fillRect(30, 30, 840, 50);

		// header
		graphic.setColor(Color.BLACK);
		graphic.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		graphic.drawString(quote.getSchedule() + ": " + work.getName() + ", " + author.getName(), 35, 65);

		// image box
		graphic.setColor(Color.GRAY);
		graphic.fillRect(30, 120, 300, 450);

		// image
		BufferedImage img = ImageIO.read(new ByteArrayInputStream(author.getAuthor().getImage()));
		Integer width = img.getWidth();
		Integer height = img.getHeight();

		if (width > 290) {
			width = 290;
			height = (width * img.getHeight()) / img.getWidth();
		}
		if (height > 440) {
			height = 440;
			width = (height * img.getWidth()) / img.getHeight();
		}

		graphic.drawImage(img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH), 35, 125, null);

		// corpus
		AttributedString attstr = new AttributedString(quote.getCorpus().replaceAll("\\<[^>]*>", ""));
		attstr.addAttribute(TextAttribute.FONT, new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		AttributedCharacterIterator iter = attstr.getIterator();
		FontRenderContext frc = graphic.getFontRenderContext();
		LineBreakMeasurer measure = new LineBreakMeasurer(iter, frc);
		measure.setPosition(iter.getBeginIndex());

		int y = 120;
		while (measure.getPosition() < iter.getEndIndex()) {
			TextLayout layout = measure.nextLayout(510);
			y += layout.getAscent();
			layout.draw(graphic, 360, y);
			y += layout.getDescent() + layout.getLeading();
		}

		// done
		ImageIO.write(image, "png", stream);
		response.getOutputStream().write(stream.toByteArray());
		response.getOutputStream().close();
	}

}
