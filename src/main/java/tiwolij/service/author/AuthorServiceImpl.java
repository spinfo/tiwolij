package tiwolij.service.author;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Author;

@Component
@Transactional
public class AuthorServiceImpl implements AuthorService {

	private final AuthorRepository authors;

	public AuthorServiceImpl(AuthorRepository authors) {
		this.authors = authors;
	}

	@Override
	public Long count() {
		return authors.count();
	}

	@Override
	public Author save(Author author) {
		return authors.save(author);
	}

	@Override
	public void delete(Integer authorId) {
		authors.delete(authorId);
	}

	@Override
	public Author getOneById(Integer authorId) {
		return authors.findOneById(authorId);
	}

	@Override
	public Author getOneBySlug(String slug) {
		return authors.findOneBySlug(slug);
	}

	@Override
	public Author getOneByWikidataId(Integer wikidataId) {
		return authors.findOneByWikidataIdWikidataId(wikidataId);
	}

	@Override
	public List<Author> getAll() {
		return authors.findAll();
	}

	@Override
	public Page<Author> getAll(Pageable pageable) {
		return authors.findAll(pageable);
	}

	@Override
	public List<Author> search(String term) {
		return authors.findAllByLocalesNameContainingIgnoreCase(term);
	}

	@Override
	public Page<Author> search(Pageable pageable, String term) {
		return authors.findAllByLocalesNameContainingIgnoreCase(pageable, term);
	}

}
