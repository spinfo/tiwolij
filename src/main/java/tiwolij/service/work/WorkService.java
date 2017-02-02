package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Author;
import tiwolij.domain.WikidataId;
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

	default public Long countByAuthor(Author author) {
		return countByAuthor(author.getId());
	}

	default public void delete(Work work) {
		delete(work.getId());
	}

	default public Boolean existsById(Integer workId) {
		return (getOneById(workId) != null);
	}

	default public Boolean existsBySlug(String slug) {
		return (getOneBySlug(slug) != null);
	}

	default public Boolean existsByWikidataId(Integer wikidataId) {
		return (getOneByWikidataId(wikidataId) != null);
	}

	default public Boolean existsByWikidataId(WikidataId wikidataId) {
		return (getOneByWikidataId(wikidataId.getId()) != null);
	}

	default public Work getOneByWikidataId(WikidataId wikidataId) {
		return getOneByWikidataId(wikidataId.getId());
	}

	default public List<Work> getAllByAuthor(Author author) {
		return getAllByAuthor(author.getId());
	}

	default public Page<Work> getAllByAuthor(Pageable pageable, Author author) {
		return getAllByAuthor(pageable, author.getId());
	}

}
