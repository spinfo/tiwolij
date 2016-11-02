package tiwolij.service.author;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.AuthorLocale;

public interface AuthorLocaleRepository extends CrudRepository<AuthorLocale, Integer> {

	public AuthorLocale findTop1ById(Integer localeId);

	public AuthorLocale findTop1ByAuthorIdAndLanguage(Integer authorId, String language);

	public List<AuthorLocale> findAll();

	public List<AuthorLocale> findAllByAuthorId(Integer authorId);

}
