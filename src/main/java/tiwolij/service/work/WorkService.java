package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;

public interface WorkService {

	public Long count();

	public Long countByAuthorId(Integer authorId);

	public Long countLocales();

	public void delLocale(Integer localeId);

	public void delWork(Integer workId);

	public WorkLocale getLocale(Integer localeId);

	public WorkLocale getLocaleByLang(Integer workId, String language);

	public List<WorkLocale> getLocales();

	public Page<WorkLocale> getLocales(Pageable pageable);

	public List<WorkLocale> getLocalesByWork(Integer workId);

	public Page<WorkLocale> getLocalesByWork(Pageable pageable, Integer workId);

	public Work getWork(Integer workId);

	public Work getWorkBySlug(String slug);

	public Work getWorkByWikidataId(Integer wikidataId);

	public List<Work> getWorks();

	public Page<Work> getWorks(Pageable pageable);

	public List<Work> getWorksByAuthor(Integer authorId);

	public Page<Work> getWorksByAuthor(Pageable pageable, Integer authorId);

	public Boolean hasLocale(Integer workId, String language);

	public Boolean hasWork(Integer workId);

	public Boolean hasWorkBySlug(String slug);

	public Boolean hasWorkByWikidataId(Integer wikidataId);

	public List<Work> search(String term);

	public WorkLocale setLocale(WorkLocale locale);

	public Work setWork(Work work);

}
