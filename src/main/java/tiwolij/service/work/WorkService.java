package tiwolij.service.work;

import java.util.List;

import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;

public interface WorkService {

	/*
	 * GETTERS
	 */

	public Work getWork(Integer workId);

	public Work getWorkBySlug(String slug);
	
	public Work getWorkByWikidataId(Integer wikidataId);

	public List<Work> getWorks();

	public List<Work> getWorksByAuthor(Integer authorId);

	public WorkLocale getLocale(Integer localeId);

	public WorkLocale getLocaleByLang(Integer workId, String language);

	public List<WorkLocale> getLocales();

	public List<WorkLocale> getLocalesByWork(Integer workId);

	/*
	 * SETTERS
	 */

	public Work setWork(Work work);

	public WorkLocale setLocale(WorkLocale locale);

	/*
	 * DELETERS
	 */

	public void delWork(Integer workId);

	public void delLocale(Integer localeId);

	/*
	 * CHECKERS
	 */

	public Boolean hasWork(Integer workId);
	
	public Boolean hasWorkBySlug(String slug);

	public Boolean hasWorkByWikidataId(Integer wikidataId);

	public Boolean hasLocale(Integer workId, String language);

	/*
	 * COUNTERS
	 */

	public Long count();

	public Long countByAuthorId(Integer authorId);

	public Long countLocales();

	/*
	 * IMPORTERS
	 */

	public Work importWorkByWikidataId(Integer wikidataId) throws Exception;

	public Work importWorkByArticle(String article) throws Exception;

	public WorkLocale importLocale(Integer workId, String language) throws Exception;

	public List<WorkLocale> importLocales(Integer workId) throws Exception;

}
