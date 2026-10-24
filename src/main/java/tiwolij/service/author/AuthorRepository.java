package tiwolij.service.author;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import tiwolij.domain.Author;

public interface AuthorRepository extends JpaRepository<Author, Integer> {

	public Author findOneById(Integer authorId);

	public Author findOneBySlug(String slug);

	public Author findOneByWikidataIdWikidataId(Integer wikidataId);

	public List<Author> findAll();

	public Page<Author> findAll(Pageable pagable);

	public List<Author> findAllByLocalesNameContainingIgnoreCase(String term);

	public Page<Author> findAllByLocalesNameContainingIgnoreCase(Pageable pageable, String term);

}
