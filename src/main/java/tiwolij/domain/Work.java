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
public class Work {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;

	@Column(nullable = false)
	private String slug;

	private String wikidataId;

	@OneToMany(mappedBy = "work")
	private List<Quote> quotes;

	@OneToMany(mappedBy = "work")
	private List<WorkLocale> locales;

	public Work() {
	}

	public Work(Author author) {
		this.author = author;
	}

	public Object get(String field) {
		Object result = null;

		try {
			result = this.getClass().getDeclaredField(field).get(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int getId() {
		return id;
	}

	public Author getAuthor() {
		return author;
	}

	public String getSlug() {
		return slug;
	}

	public String getWikidataId() {
		return wikidataId;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public List<WorkLocale> getLocales() {
		return locales;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setWikidataId(String wikidataId) {
		this.wikidataId = wikidataId.toUpperCase();
	}

	public void setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
	}

	public void setLocales(List<WorkLocale> locales) {
		this.locales = locales;
	}

}
