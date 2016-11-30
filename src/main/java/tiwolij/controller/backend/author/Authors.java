package tiwolij.controller.backend.author;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.wikidata.wdtk.wikibaseapi.apierrors.NoSuchEntityErrorException;

import tiwolij.domain.Author;
import tiwolij.service.author.AuthorService;

@Controller
@RequestMapping("/tiwolij/authors")
public class Authors {

	@Autowired
	private AuthorService authors;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/authors/list";
	}

	@GetMapping("/list")
	public ModelAndView list(Pageable pageable) {
		ModelAndView mv = new ModelAndView("backend/author/author_list");
		Page<Author> page = authors.getAuthors(pageable);

		mv.addObject("authors", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "authorId") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/author/author_view");

		mv.addObject("author", authors.getAuthor(authorId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create() {
		ModelAndView mv = new ModelAndView("backend/author/author_create");

		mv.addObject("author", new Author());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Author author) {
		author = authors.setAuthor(author);
		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@PostMapping("/import")
	public String imp0rt(@ModelAttribute Author author) throws Exception {
		if (author.getWikidataId() == null)
			throw new NoSuchEntityErrorException("No Wikidata ID given");

		author = authors.importAuthorByWikidataId(author.getWikidataId());
		authors.importLocales(author.getId());

		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "authorId") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/author/author_edit");

		mv.addObject("author", authors.getAuthor(authorId).setImage(new byte[0]));
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Author author) throws Exception {
		String url = new String(author.getImage());
		author = (!url.isEmpty()) ? authors.importImage(author.getId(), new URL(url))
				: author.setImage(authors.getAuthor(author.getId()).getImage());

		authors.setAuthor(author);
		return "redirect:/tiwolij/authors/view?authorId=" + author.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "authorId") Integer authorId) {
		authors.delAuthor(authorId);
		return "redirect:/tiwolij/authors/list";
	}

}
