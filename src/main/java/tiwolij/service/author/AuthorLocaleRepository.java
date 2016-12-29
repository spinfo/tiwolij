package tiwolij.service.author;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import tiwolij.domain.AuthorLocale;

public interface AuthorLocaleRepository extends PagingAndSortingRepository<AuthorLocale, Integer> {

	public List<AuthorLocale> findAll();

	public Page<AuthorLocale> findAll(Pageable pageable);

	public List<AuthorLocale> findAllByAuthorId(Integer authorId);

	public Page<AuthorLocale> findAllByAuthorId(Pageable pageable, Integer authorId);

	public List<AuthorLocale> findAllByNameContainingIgnoreCase(String name);

	public AuthorLocale findTop1ByAuthorIdAndLanguage(Integer authorId, String language);

	public AuthorLocale findTop1ById(Integer localeId);

}
