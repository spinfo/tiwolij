package tiwolij.service.work;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import tiwolij.domain.Work;

@Component
@Transactional
public class WorkServiceImpl implements WorkService {

	private final WorkRepository works;

	public WorkServiceImpl(WorkRepository works) {
		this.works = works;
	}

	@Override
	public Long count() {
		return works.count();
	}

	@Override
	public Long countByAuthor(Integer authorId) {
		return works.countByAuthorId(authorId);
	}

	@Override
	public Work save(Work work) {
		return works.save(work);
	}

	@Override
	public void delete(Integer workId) {
		works.delete(workId);
	}

	@Override
	public Work getOneById(Integer workId) {
		return works.findOneById(workId);
	}

	@Override
	public Work getOneBySlug(String slug) {
		return works.findOneBySlug(slug);
	}

	@Override
	public Work getOneByWikidataId(Integer wikidataId) {
		return works.findOneByWikidataIdWikidataId(wikidataId);
	}

	@Override
	public List<Work> getAll() {
		return works.findAll();
	}

	@Override
	public Page<Work> getAll(Pageable pageable) {
		return works.findAll(pageable);
	}

	@Override
	public List<Work> getAllByAuthor(Integer authorId) {
		return works.findAllByAuthorId(authorId);
	}

	@Override
	public Page<Work> getAllByAuthor(Pageable pageable, Integer authorId) {
		return works.findAllByAuthorId(pageable, authorId);
	}

	@Override
	public List<Work> search(String term) {
		return works.findAllByLocalesNameContainingIgnoreCase(term);
	}

	@Override
	public Page<Work> search(Pageable pageable, String term) {
		return works.findAllByLocalesNameContainingIgnoreCase(pageable, term);
	}

}
