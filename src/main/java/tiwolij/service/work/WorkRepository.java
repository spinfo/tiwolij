package tiwolij.service.work;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Work;

public interface WorkRepository extends CrudRepository<Work, Integer> {

	public Work findOneById(Integer workId);

	public Work findOneByWikidataId(Integer wikidataId);
	
	public List<Work> findAll();

	public List<Work> findAllByAuthorId(Integer authorId);

}
