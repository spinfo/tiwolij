package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.WorkLocale;

public interface WorkLocaleRepository extends PagingAndSortingRepository<WorkLocale, Integer> {

	public WorkLocale findTop1ById(Integer localeId);

	public WorkLocale findTop1ByWorkIdAndLanguage(Integer workId, String language);

	public List<WorkLocale> findAll();

	public List<WorkLocale> findAllByWorkId(Integer workId);

	// pagination

	public Page<WorkLocale> findAll(Pageable pageable);

	public Page<WorkLocale> findAllByWorkId(Pageable pageable, Integer workId);

}
