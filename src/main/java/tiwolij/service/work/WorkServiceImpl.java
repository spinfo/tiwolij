package tiwolij.service.work;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;

@Component
@Transactional
public class WorkServiceImpl implements WorkService {

	private final WorkLocaleRepository locales;

	private final WorkRepository works;

	public WorkServiceImpl(WorkLocaleRepository locales, WorkRepository works) {
		this.locales = locales;
		this.works = works;
	}

	@Override
	public Long count() {
		return works.count();
	}

	@Override
	public Long countByAuthorId(Integer authorId) {
		return new Long(works.findAllByAuthorId(authorId).size());
	}

	@Override
	public Long countLocales() {
		return locales.count();
	}

	@Override
	public void delLocale(Integer localeId) {
		locales.delete(localeId);
	}

	@Override
	public void delWork(Integer workId) {
		works.delete(workId);
	}

	@Override
	public WorkLocale getLocale(Integer localeId) {
		return locales.findTop1ById(localeId);
	}

	@Override
	public WorkLocale getLocaleByLang(Integer workId, String language) {
		if (!hasLocale(workId, language))
			return null;

		return locales.findTop1ByWorkIdAndLanguage(workId, language);
	}

	@Override
	public List<WorkLocale> getLocales() {
		return locales.findAll();
	}

	@Override
	public Page<WorkLocale> getLocales(Pageable pageable) {
		return locales.findAll(pageable);
	}

	@Override
	public List<WorkLocale> getLocalesByWork(Integer workId) {
		return locales.findAllByWorkId(workId);
	}

	@Override
	public Page<WorkLocale> getLocalesByWork(Pageable pageable, Integer workId) {
		return locales.findAllByWorkId(pageable, workId);
	}

	@Override
	public Work getWork(Integer workId) {
		return works.findTop1ById(workId);
	}

	@Override
	public Work getWorkBySlug(String slug) {
		return works.findTop1BySlug(slug);
	}

	@Override
	public Work getWorkByWikidataId(Integer wikidataId) {
		return works.findTop1ByWikidataId(wikidataId);
	}

	@Override
	public List<Work> getWorks() {
		return works.findAll();
	}

	@Override
	public Page<Work> getWorks(Pageable pageable) {
		return works.findAll(pageable);
	}

	@Override
	public List<Work> getWorksByAuthor(Integer authorId) {
		return works.findAllByAuthorId(authorId);
	}

	@Override
	public Page<Work> getWorksByAuthor(Pageable pageable, Integer authorId) {
		return works.findAllByAuthorId(pageable, authorId);
	}

	@Override
	public Boolean hasLocale(Integer workId, String language) {
		return locales.findTop1ByWorkIdAndLanguage(workId, language) != null;
	}

	@Override
	public Boolean hasWork(Integer workId) {
		return works.exists(workId);
	}

	@Override
	public Boolean hasWorkBySlug(String slug) {
		return works.findTop1BySlug(slug) != null;
	}

	@Override
	public Boolean hasWorkByWikidataId(Integer wikidataId) {
		return works.findTop1ByWikidataId(wikidataId) != null;
	}

	@Override
	public List<Work> search(String term) {
		List<WorkLocale> found = locales.findAllByNameContainingIgnoreCase(term);
		List<Work> result = new ArrayList<Work>();

		for (WorkLocale l : found)
			if (!result.contains(l.getWork()))
				result.add(l.getWork());

		return result;
	}

	@Override
	public WorkLocale setLocale(WorkLocale locale) {
		return locales.save(locale);
	}

	@Override
	public Work setWork(Work work) {
		return works.save(work);
	}

}
