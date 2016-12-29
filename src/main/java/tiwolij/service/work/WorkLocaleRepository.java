package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.WorkLocale;

public interface WorkLocaleRepository extends PagingAndSortingRepository<WorkLocale, Integer> {

	public List<WorkLocale> findAll();

	public Page<WorkLocale> findAll(Pageable pageable);

	public List<WorkLocale> findAllByNameContainingIgnoreCase(String name);

	public List<WorkLocale> findAllByWorkId(Integer workId);

	public Page<WorkLocale> findAllByWorkId(Pageable pageable, Integer workId);

	public WorkLocale findTop1ById(Integer localeId);

	public WorkLocale findTop1ByWorkIdAndLanguage(Integer workId, String language);

}
