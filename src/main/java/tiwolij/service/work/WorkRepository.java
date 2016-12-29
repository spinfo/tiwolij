package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.Work;

public interface WorkRepository extends PagingAndSortingRepository<Work, Integer> {

	public List<Work> findAll();

	public Page<Work> findAll(Pageable pageable);

	public List<Work> findAllByAuthorId(Integer authorId);

	public Page<Work> findAllByAuthorId(Pageable pageable, Integer authorId);

	public Work findTop1ById(Integer workId);

	public Work findTop1BySlug(String slug);

	public Work findTop1ByWikidataId(Integer wikidataId);

}
