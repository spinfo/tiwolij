package tiwolij.service.locale;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.Locale;

public interface LocaleRepository extends PagingAndSortingRepository<Locale, Integer> {

	@Query("SELECT COUNT(x) FROM Locale x WHERE x.author IS NOT NULL")
	public Long countByAuthors();
	
	public Long countByAuthorId(Integer authorId);

	@Query("SELECT COUNT(x) FROM Locale x WHERE x.work IS NOT NULL")
	public Long countByWorks();
	
	public Long countByWorkId(Integer workId);

	public Locale findOneById(Integer localeId);

	public Locale findOneByAuthorIdAndLanguage(Integer authorId, String language);

	public Locale findOneByWorkIdAndLanguage(Integer authorId, String language);

	public List<Locale> findAll();

	public Page<Locale> findAll(Pageable pageable);

	public List<Locale> findAllByAuthorId(Integer authorId);

	public Page<Locale> findAllByAuthorId(Pageable pageable, Integer authorId);

	public List<Locale> findAllByWorkId(Integer workId);

	public Page<Locale> findAllByWorkId(Pageable pageable, Integer workId);

}
