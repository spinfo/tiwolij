package tiwolij.service;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import tiwolij.domain.Quote;
import tiwolij.domain.Work;

public interface QuoteRepository extends CrudRepository<Quote, Integer> {

	public List<Quote> findAll();

	public Quote findById(int id);

	public List<Quote> findByWork(Work work);

	public List<Quote> findBySchedule(Date schedule);

}
