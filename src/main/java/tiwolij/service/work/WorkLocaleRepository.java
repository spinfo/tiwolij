package tiwolij.service.work;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.WorkLocale;

public interface WorkLocaleRepository extends CrudRepository<WorkLocale, Integer> {

	public WorkLocale findTop1ById(Integer localeId);

	public WorkLocale findTop1ByWorkIdAndLanguage(Integer workId, String language);

	public List<WorkLocale> findAll();

	public List<WorkLocale> findAllByWorkId(Integer workId);

}
