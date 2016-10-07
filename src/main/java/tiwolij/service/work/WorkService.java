package tiwolij.service.work;

import java.util.List;

import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;

public interface WorkService {

	/*
	 * GETTERS
	 */

	public Long getCount();

	public Work getWork(Integer workId);

	public Work getWorkByWikidata(Integer wikidataId);

	public List<Work> getWorks();

	public List<Work> getWorksByAuthor(Integer authorId);

	public Long getLocaleCount();

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

	public Boolean hasLocale(Integer workId, String language);

	/*
	 * IMPORTERS
	 */

	public Work importWork(Integer wikidataId) throws Exception;

	public WorkLocale importLocale(Integer workId, String language) throws Exception;

	public List<WorkLocale> importLocales(Integer workId) throws Exception;

}
