package tiwolij.domain;

import java.util.ArrayList;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import tiwolij.util.StringEncoding;

@Entity
@Table(name = "works")
public class Work {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String slug;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;

	@OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
	private List<Quote> quotes;

	@OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
	private List<Locale> locales;

	@OneToOne(mappedBy = "work", cascade = CascadeType.ALL)
	private WikidataId wikidataId;

	public Work() {
	}

	public Work(Author author) {
		this.author = author;
	}

	public Integer getId() {
		return id;
	}

	public String getSlug() {
		return slug;
	}

	public Author getAuthor() {
		return author;
	}

	public List<Quote> getQuotes() {
		return quotes;
	}

	public List<Locale> getLocales() {
		return locales;
	}

	public Map<String, Locale> getMappedLocales() {
		return (locales != null) ? locales.stream().collect(Collectors.toMap(Locale::getLanguage, i -> i)) : null;
	}

	public Integer getWikidataId() {
		return (wikidataId != null) ? wikidataId.getWikidataId() : null;
	}

	public Work setId(Integer id) {
		this.id = id;
		return this;
	}

	public Work setSlug(String slug) {
		this.slug = StringEncoding.toSlug(slug);
		return this;
	}

	public Work setAuthor(Author author) {
		this.author = author;
		return this;
	}

	public Work setLocales(List<Locale> locales) {
		this.locales = locales;
		return this;
	}

	public Work setWikidataId(Integer wikidataId) {
		if (wikidataId == null) {
			return this;
		}

		if (this.wikidataId == null) {
			this.wikidataId = new WikidataId(this);
		}

		this.wikidataId.setWikidataId(wikidataId);
		return this;
	}

	public Work addQuote(Quote quote) {
		if (this.quotes == null) {
			this.quotes = new ArrayList<Quote>();
		}

		this.quotes.add(quote.setWork(this));
		return this;
	}

	public Work addLocale(Locale locale) throws Exception {
		if (this.locales == null) {
			this.locales = new ArrayList<Locale>();
		}

		if (!getMappedLocales().containsKey(locale.getLanguage())) {
			this.locales.add(locale.setWork(this));
		}

		return this;
	}

}
