package tiwolij.service.author;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Author;

public interface AuthorRepository extends CrudRepository<Author, Integer> {

	public Author findOneById(Integer authorId);

	public Author findOneBySlug(String slug);
	
	public Author findOneByWikidataId(Integer wikidataId);

	public List<Author> findAll();

}
