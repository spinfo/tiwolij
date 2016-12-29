package tiwolij.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
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

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	protected Author author;

	@Id
	@GeneratedValue
	protected Integer id;

	@OneToMany(mappedBy = "work")
	protected List<WorkLocale> locales;

	@OneToMany(mappedBy = "work")
	protected List<Quote> quotes;

	@Column(nullable = false)
	protected String slug;

	protected Integer wikidataId;

	public Work() {
	}

	public Work(Author author) {
		this.author = author;
	}

	public Author getAuthor() {
		return author;
	}

	public Integer getId() {
		return id;
	}

	public Map<String, WorkLocale> getLocales() {
		return locales != null ? locales.stream().collect(Collectors.toMap(WorkLocale::getLanguage, l -> l)) : null;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public String getSlug() {
		return slug;
	}

	public Integer getWikidataId() {
		return wikidataId;
	}

	public Work setAuthor(Author author) {
		this.author = author;
		return this;
	}

	public Work setId(Integer id) {
		this.id = id;
		return this;
	}

	public Work setLocales(List<WorkLocale> locales) {
		this.locales = locales;
		return this;
	}

	public Work setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
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

}
