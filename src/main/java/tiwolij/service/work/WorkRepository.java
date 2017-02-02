package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.Work;

public interface WorkRepository extends PagingAndSortingRepository<Work, Integer> {

	public Long countByAuthorId(Integer authorId);

	public Work findOneById(Integer workId);

	public Work findOneBySlug(String slug);

	public Work findOneByWikidataIdWikidataId(Integer wikidataId);

	public List<Work> findAll();

	public List<Work> findAllByAuthorId(Integer authorId);

	public Page<Work> findAllByAuthorId(Pageable pageable, Integer authorId);
	
	public List<Work> findAllByLocalesNameContainingIgnoreCase(String term);
	
	public Page<Work> findAllByLocalesNameContainingIgnoreCase(Pageable pageable, String term);

}
