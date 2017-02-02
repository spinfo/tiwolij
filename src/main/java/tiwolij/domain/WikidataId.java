package tiwolij.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

@Entity
@Table(name = "wikidataids")
public class WikidataId {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private Integer wikidataId;

	@OneToOne
	@JoinColumn(name = "author_id")
	private Author author;

	@OneToOne
	@JoinColumn(name = "work_id")
	private Work work;

	public WikidataId() {
	}

	public WikidataId(Author author) {
		this.author = author;
	}

	public WikidataId(Work work) {
		this.work = work;
	}

	public Integer getId() {
		return id;
	}

	public Integer getWikidataId() {
		return wikidataId;
	}

	public Author getAuthor() {
		return author;
	}

	public Work getWork() {
		return work;
	}

	public WikidataId setId(Integer id) {
		this.id = id;
		return this;
	}

	public WikidataId setWikidataId(Integer wikidataId) {
		this.wikidataId = wikidataId;
		return this;
	}

	public WikidataId setAuthor(Author author) throws Exception {
		if (work != null) {
			throw new MySQLIntegrityConstraintViolationException();
		}

		this.author = author;
		return this;
	}

	public WikidataId setWork(Work work) throws Exception {
		if (author != null) {
			throw new MySQLIntegrityConstraintViolationException();
		}

		this.work = work;
		return this;
	}

}
