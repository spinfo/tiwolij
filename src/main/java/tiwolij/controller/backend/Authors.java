package tiwolij.controller.backend;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Author;
import tiwolij.service.author.AuthorService;
import tiwolij.util.ImageLoader;

@Controller
@RequestMapping("/tiwolij/authors")
public class Authors {

	@Autowired
	private Environment env;

	@Autowired
	private AuthorService authors;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/authors/list";
	}

	@GetMapping("/list")
	public ModelAndView list(Pageable pageable) {
		ModelAndView mv = new ModelAndView("backend/author/list");
		Page<Author> page = authors.getAll(pageable);

		mv.addObject("page", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "authorId") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/author/view");

		mv.addObject("author", authors.getOneById(authorId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create() {
		ModelAndView mv = new ModelAndView("backend/author/create");

		mv.addObject("author", new Author());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Author author) {
		author = authors.save(author);
		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "authorId") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/author/edit");

		mv.addObject("author", authors.getOneById(authorId).setImage(new byte[0]));
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Author edited) throws Exception {
		Author author = authors.getOneById(edited.getId());
		String image = new String(edited.getImage());
		Integer height = Integer.parseInt(env.getProperty("tiwolij.import.imageheight"));

		if (edited.getWikidataId() != author.getWikidataId()) {
			author.setWikidataId(edited.getWikidataId());
		}

		if (edited.getSlug() != author.getSlug()) {
			author.setSlug(edited.getSlug());
		}

		if (!image.isEmpty()) {
			edited.setImage(ImageLoader.getBytes(new URL(image), height));
		} else {
			edited.setImage(author.getImage());
		}

		if (edited.getImageAttribution() != author.getImageAttribution()) {
			author.setImageAttribution(edited.getImageAttribution());
		}

		authors.save(author);
		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "authorId") Integer authorId) {
		authors.delete(authorId);
		return "redirect:/tiwolij/authors/list";
	}

}
