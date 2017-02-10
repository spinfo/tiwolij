package tiwolij.service.author;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tiwolij.domain.Author;

public interface AuthorService {

	public Long count();

	public Author save(Author author);

	public void delete(Integer authorId);

	public Author getOneById(Integer authorId);

	public Author getOneBySlug(String slug);

	public Author getOneByWikidataId(Integer wikidataId);

	public List<Author> getAll();

	public Page<Author> getAll(Pageable pageable);

	public List<Author> search(String term);

	public Page<Author> search(Pageable pagable, String term);

	default public Boolean existsById(Integer authorId) {
		return (authorId != null && getOneById(authorId) != null);
	}

	default public Boolean existsBySlug(String slug) {
		return (slug != null && getOneBySlug(slug) != null);
	}

	default public Boolean existsByWikidataId(Integer wikidataId) {
		return (wikidataId != null && getOneByWikidataId(wikidataId) != null);
	}

}
