package tiwolij.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "works")
public class Work extends BaseEntity {

	@Id
	@GeneratedValue
	protected Integer id;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	protected Author author;

	@Column(nullable = false)
	protected String slug;

	protected Integer wikidataId;

	@OneToMany(mappedBy = "work")
	protected List<Quote> quotes;

	@OneToMany(mappedBy = "work")
	protected List<WorkLocale> locales;

	public Work() {
	}

	public Work(Author author) {
		this.author = author;
	}

	public Integer getId() {
		return id;
	}

	public Author getAuthor() {
		return author;
	}

	public String getSlug() {
		return slug;
	}

	public Integer getWikidataId() {
		return wikidataId;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public List<WorkLocale> getLocales() {
		return locales;
	}

	public Work setId(Integer id) {
		this.id = id;
		return this;
	}

	public Work setAuthor(Author author) {
		this.author = author;
		return this;
	}

	public Work setSlug(String slug) {
		this.slug = slug;
		return this;
	}

	public Work setWikidataId(Integer wikidataId) {
		this.wikidataId = wikidataId;
		return this;
	}

	public Work setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
		return this;
	}

	public Work setLocales(List<WorkLocale> locales) {
		this.locales = locales;
		return this;
	}

}
