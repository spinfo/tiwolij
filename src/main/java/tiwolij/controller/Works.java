package tiwolij.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import tiwolij.domain.Work;
import tiwolij.service.author.AuthorService;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/tiwolij/works")
public class Works {

	@Autowired
	private AuthorService authors;

	@Autowired
	private WorkService works;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/works/list";
	}

	@GetMapping("/list")
	public ModelAndView list(@RequestParam(name = "order", defaultValue = "id") String order,
			@RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("works/list");
		List<Work> list = new ArrayList<Work>();

		if (authorId != 0) {
			list = works.getWorksByAuthor(authorId);
		} else {
			list = works.getWorks();
		}

		list.sort((x, y) -> x.compareNaturalBy(y, order));
		mv.addObject("works", list);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "workId") Integer workId) {
		ModelAndView mv = new ModelAndView("works/view");

		mv.addObject("work", works.getWork(workId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("works/create");
		Work work = (authors.hasAuthor(authorId)) ? new Work(authors.getAuthor(authorId)) : new Work();

		mv.addObject("work", work);
		mv.addObject("authors", authors.getAuthors());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Work work) {
		work = works.setWork(work);

		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

	@PostMapping("/import")
	public String imp0rt(@ModelAttribute Work work) throws Exception {
		if (work.getWikidataId() != null)
			work = works.importWork(work.getWikidataId());

		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "workId") Integer workId) {
		ModelAndView mv = new ModelAndView("works/edit");

		mv.addObject("work", works.getWork(workId));
		mv.addObject("authors", authors.getAuthors());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Work work) throws Exception {
		work = works.setWork(work);
		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "workId") Integer workId) {
		works.delWork(workId);
		return "redirect:/tiwolij/works/list";
	}

}
