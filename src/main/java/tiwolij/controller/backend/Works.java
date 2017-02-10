package tiwolij.controller.backend;

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
	public ModelAndView list(Pageable pageable, @RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/work/list");
		Page<Work> page;

		if (authorId >= 0 && authors.existsById(authorId)) {
			page = works.getAllByAuthor(pageable, authorId);
		} else {
			page = works.getAll(pageable);
		}

		mv.addObject("page", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "workId") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/work/view");

		mv.addObject("work", works.getOneById(workId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "authorId", defaultValue = "0") Integer authorId) {
		ModelAndView mv = new ModelAndView("backend/work/create");
		Work work;

		if (authorId > 0 && authors.existsById(authorId)) {
			work = new Work(authors.getOneById(authorId));
		} else {
			work = new Work();
		}

		mv.addObject("work", work);
		mv.addObject("authors", authors.getAll());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute Work work) {
		work = works.save(work);
		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "workId") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/work/edit");

		mv.addObject("work", works.getOneById(workId));
		mv.addObject("authors", authors.getAll());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute Work work) throws Exception {

		work = works.getOneById(work.getId()).merge(work);
		work = works.save(work);

		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "workId") Integer workId) {
		works.delete(workId);
		return "redirect:/tiwolij/works/list";
	}

}
