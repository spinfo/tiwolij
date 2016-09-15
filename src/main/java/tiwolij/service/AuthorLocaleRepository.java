package tiwolij.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Author;
import tiwolij.domain.AuthorLocale;

public interface AuthorLocaleRepository extends CrudRepository<AuthorLocale, Integer> {

	public AuthorLocale findById(int id);

	public List<AuthorLocale> findByAuthor(Author author);

	public List<AuthorLocale> findAll();
	
}
