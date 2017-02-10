package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Work;

public interface WorkService {

	public Long count();

	public Long countByAuthor(Integer authorId);

	public Work save(Work work);

	public void delete(Integer workId);

	public Work getOneById(Integer workId);

	public Work getOneBySlug(String slug);

	public Work getOneByWikidataId(Integer wikidataId);

	public List<Work> getAll();

	public Page<Work> getAll(Pageable pageable);

	public List<Work> getAllByAuthor(Integer authorId);

	public Page<Work> getAllByAuthor(Pageable pageable, Integer authorId);

	public List<Work> search(String term);

	public Page<Work> search(Pageable pageable, String term);

	default public Boolean existsById(Integer workId) {
		return (workId != null && getOneById(workId) != null);
	}

	default public Boolean existsBySlug(String slug) {
		return (slug != null && getOneBySlug(slug) != null);
	}

	default public Boolean existsByWikidataId(Integer wikidataId) {
		return (wikidataId != null && getOneByWikidataId(wikidataId) != null);
	}

}
