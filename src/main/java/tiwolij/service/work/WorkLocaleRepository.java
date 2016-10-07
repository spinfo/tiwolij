package tiwolij.service.work;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.WorkLocale;

public interface WorkLocaleRepository extends CrudRepository<WorkLocale, Integer> {

	public WorkLocale findOneById(Integer localeId);

	public WorkLocale findOneByWorkIdAndLanguage(Integer workId, String language);

	public List<WorkLocale> findAll();

	public List<WorkLocale> findAllByWorkId(Integer workId);

}
