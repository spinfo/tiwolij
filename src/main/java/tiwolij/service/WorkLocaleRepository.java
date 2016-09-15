package tiwolij.service;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Work;
import tiwolij.domain.WorkLocale;

public interface WorkLocaleRepository extends CrudRepository<WorkLocale, Integer> {

	public WorkLocale findById(int id);

	public List<WorkLocale> findByWork(Work work);

	public List<WorkLocale> findAll();

}
