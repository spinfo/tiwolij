package tiwolij.service.author;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.AuthorLocale;

public interface AuthorLocaleRepository extends CrudRepository<AuthorLocale, Integer> {

	public AuthorLocale findOneById(Integer localeId);

	public AuthorLocale findOneByAuthorIdAndLanguage(Integer authorId, String language);

	public List<AuthorLocale> findAll();

	public List<AuthorLocale> findAllByAuthorId(Integer authorId);

}
