package tiwolij.service.work;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Work;

public interface WorkRepository extends CrudRepository<Work, Integer> {

	public Work findTop1ById(Integer workId);

	public Work findTop1BySlug(String slug);
	
	public Work findTop1ByWikidataId(Integer wikidataId);
	
	public List<Work> findAll();

	public List<Work> findAllByAuthorId(Integer authorId);

}
