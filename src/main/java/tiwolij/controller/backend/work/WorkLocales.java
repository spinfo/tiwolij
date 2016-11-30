package tiwolij.controller.backend.work;

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
import tiwolij.domain.WorkLocale;
import tiwolij.service.work.WorkService;

@Controller
@RequestMapping("/tiwolij/works/locales")
public class WorkLocales {

	@Autowired
	private WorkService works;

	@GetMapping({ "", "/" })
	public String root() {
		return "redirect:/tiwolij/works/locales/list";
	}

	@GetMapping("/list")
	public ModelAndView list(Pageable pageable, @RequestParam(name = "id", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/work/locale_list");
		Page<WorkLocale> page = works.hasWork(workId) ? works.getLocalesByWork(pageable, workId)
				: works.getLocales(pageable);

		mv.addObject("locales", page);
		return mv;
	}

	@GetMapping("/view")
	public ModelAndView view(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/work/locale_view");

		mv.addObject("locale", works.getLocale(localeId));
		return mv;
	}

	@GetMapping("/create")
	public ModelAndView create(@RequestParam(name = "workId", defaultValue = "0") Integer workId) {
		ModelAndView mv = new ModelAndView("backend/work/locale_create");
		WorkLocale locale = (works.hasWork(workId)) ? new WorkLocale(works.getWork(workId)) : new WorkLocale();

		mv.addObject("locale", locale);
		mv.addObject("works", works.getWorks());
		return mv;
	}

	@PostMapping("/create")
	public String create(@ModelAttribute WorkLocale locale) {
		works.setLocale(locale);
		return "redirect:/tiwolij/works/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/import")
	public String imp0rt(@RequestParam(name = "workId") Integer workId) throws Exception {
		works.importLocales(workId);
		return "redirect:/tiwolij/works/view?workId=" + workId;
	}

	@GetMapping("/edit")
	public ModelAndView edit(@RequestParam(name = "localeId") Integer localeId) {
		ModelAndView mv = new ModelAndView("backend/work/locale_edit");

		mv.addObject("locale", works.getLocale(localeId));
		mv.addObject("works", works.getWorks());
		return mv;
	}

	@PostMapping("/edit")
	public String edit(@ModelAttribute WorkLocale locale) {
		works.setLocale(locale);
		return "redirect:/tiwolij/works/locales/view?localeId=" + locale.getId();
	}

	@GetMapping("/delete")
	public String delete(@RequestParam(name = "localeId") Integer localeId) {
		Work work = works.getLocale(localeId).getWork();

		works.delLocale(localeId);
		return "redirect:/tiwolij/works/view?workId=" + work.getId();
	}

}
