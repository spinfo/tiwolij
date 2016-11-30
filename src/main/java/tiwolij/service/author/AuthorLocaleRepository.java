package tiwolij.service.author;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.AuthorLocale;

public interface AuthorLocaleRepository extends PagingAndSortingRepository<AuthorLocale, Integer> {

	public AuthorLocale findTop1ById(Integer localeId);

	public AuthorLocale findTop1ByAuthorIdAndLanguage(Integer authorId, String language);

	public List<AuthorLocale> findAll();

	public List<AuthorLocale> findAllByAuthorId(Integer authorId);

	// pagination

	public Page<AuthorLocale> findAll(Pageable pageable);

	public Page<AuthorLocale> findAllByAuthorId(Pageable pageable, Integer authorId);

	// search

	public List<AuthorLocale> findAllByNameContainingIgnoreCase(String name);

}
