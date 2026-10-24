package tiwolij.domain;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "wikidataids")
public class WikidataId implements Serializable {

	private static final long serialVersionUID = 8407720903534196950L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
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
			throw new SQLIntegrityConstraintViolationException();
		}

		this.author = author;
		return this;
	}

	public WikidataId setWork(Work work) throws Exception {
		if (author != null) {
			throw new SQLIntegrityConstraintViolationException();
		}

		this.work = work;
		return this;
	}

}
