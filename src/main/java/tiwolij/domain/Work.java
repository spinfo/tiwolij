package tiwolij.domain;

import java.io.Serializable;
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

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import tiwolij.util.StringEncoding;

@Entity
@Table(name = "works")
public class Work implements Serializable {

	private static final long serialVersionUID = -2333662636408115444L;

	@Id
	@GeneratedValue
	@JsonIgnore
	private Integer id;

	@Column(nullable = false)
	private String slug;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;

	@OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
	@JsonBackReference("quote")
	private List<Quote> quotes;

	@OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
	@JsonManagedReference("work_locale")
	private List<Locale> locales;

	@OneToOne(mappedBy = "work", cascade = CascadeType.ALL)
	private WikidataId wikidataId;

	public Work() {
	}

	public Work(Author author) {
		this.author = author;
	}

	public Work merge(Work work) {
		setId(ObjectUtils.firstNonNull(work.getId(), getId()));
		setSlug(ObjectUtils.firstNonNull(work.getSlug(), getSlug()));
		setAuthor(ObjectUtils.firstNonNull(work.getAuthor(), getAuthor()));
		setQuotes(ObjectUtils.firstNonNull(work.getQuotes(), getQuotes()));
		setLocales(ObjectUtils.firstNonNull(work.getLocales(), getLocales()));
		setWikidataId(ObjectUtils.firstNonNull(work.getWikidataId(), getWikidataId()));

		return this;
	}

	public Boolean hasId() {
		return (id != null && id > 0);
	}

	public Boolean hasSlug() {
		return (slug != null && !slug.isEmpty());
	}

	public Boolean hasAuthor() {
		return (author != null);
	}

	public Boolean hasQuotes() {
		return (quotes != null && !quotes.isEmpty());
	}

	public Boolean hasLocale(String language) {
		return (hasLocales() && getMappedLocales().containsKey(language));
	}

	public Boolean hasLocales() {
		return (locales != null && !locales.isEmpty());
	}

	public Boolean hasWikidataId() {
		return (wikidataId != null && wikidataId.getWikidataId() != null && wikidataId.getWikidataId() > 0);
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

	@JsonIgnore
	public Map<String, Locale> getMappedLocales() {
		return (hasLocales()) ? locales.stream().collect(Collectors.toMap(Locale::getLanguage, i -> i)) : null;
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

	public Work setQuotes(List<Quote> quotes) {
		this.quotes = quotes;
		return this;
	}

	public Work setLocales(List<Locale> locales) {
		this.locales = locales;
		return this;
	}

	public Work setWikidataId(Integer wikidataId) {
		if (wikidataId != null) {
			if (this.wikidataId == null) {
				this.wikidataId = new WikidataId(this);
			}

			this.wikidataId.setWikidataId(wikidataId);
		}

		return this;
	}

	public Work addQuote(Quote quote) {
		if (quote != null) {
			if (this.quotes == null) {
				this.quotes = new ArrayList<Quote>();
			}

			this.quotes.add(quote.setWork(this));
		}

		return this;
	}

	public Work addLocale(Locale locale) {
		if (locale != null) {
			if (this.locales == null) {
				this.locales = new ArrayList<Locale>();
			}

			if (!hasLocale(locale.getLanguage())) {
				this.locales.add(locale.setWork(this));
			}
		}

		return this;
	}

}
