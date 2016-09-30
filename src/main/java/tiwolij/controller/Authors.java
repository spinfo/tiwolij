package tiwolij.controller;

import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	public ModelAndView list(@RequestParam(name = "order", defaultValue = "id") String order) throws Exception {
		ModelAndView mv = new ModelAndView("authors/list");
		List<Author> list = authors.getAuthors();
		list.sort((x, y) -> x.get(order).toString().compareTo(y.get(order).toString()));

		mv.addObject("authors", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "id") Integer id) {
		ModelAndView mv = new ModelAndView("authors/view");

		mv.addObject("author", authors.getAuthor(id));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create() {
		ModelAndView mv = new ModelAndView("authors/create");

		mv.addObject("author", new Author());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Author author) throws Exception {
		author = (author.getWikidataId() != null) ? authors.importAuthor(author.getWikidataId())
				: authors.setAuthor(author);

		return "redirect:/tiwolij/authors/view?id=" + author.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "id") Integer id) {
		ModelAndView mv = new ModelAndView("authors/edit");

		mv.addObject("author", authors.getAuthor(id).setImage(new byte[0]));
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Author author) throws Exception {
		String url = new String(author.getImage());
		author = (!url.isEmpty()) ? authors.importImage(author.getId(), new URL(url))
				: author.setImage(authors.getAuthor(author.getId()).getImage());

		authors.setAuthor(author);
		return "redirect:/tiwolij/authors/view?id=" + author.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "id") Integer id) {
		authors.delAuthor(id);
		return "redirect:/tiwolij/authors/list";
	}

}
