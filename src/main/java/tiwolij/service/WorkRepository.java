package tiwolij.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Author;
import tiwolij.domain.Work;

public interface WorkRepository extends CrudRepository<Work, Integer> {

	public Work findById(int id);

	public List<Work> findByAuthor(Author author);

	public Work findBySlug(String slug);

	public List<Work> findAll();

}
