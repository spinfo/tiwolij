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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import tiwolij.util.StringEncoding;

@Entity
@Table(name = "authors")
public class Author {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String slug;

	@Lob
	private byte[] image;

	private String imageAttribution;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Work> works = new ArrayList<Work>();

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Locale> locales = new ArrayList<Locale>();

	@OneToOne(mappedBy = "author", cascade = CascadeType.ALL)
	private WikidataId wikidataId;

	public Author() {
	}

	public Integer getId() {
		return id;
	}

	public String getSlug() {
		return slug;
	}

	public byte[] getImage() {
		return image;
	}

	public String getImageAttribution() {
		return imageAttribution;
	}

	public List<Work> getWorks() {
		return works;
	}

	public List<Quote> getQuotes() {
		return works.stream().map(i -> i.getQuotes()).flatMap(List::stream).collect(Collectors.toList());
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

	public Author setId(Integer id) {
		this.id = id;
		return this;
	}

	public Author setSlug(String slug) {
		this.slug = StringEncoding.toSlug(slug);
		return this;
	}

	public Author setImage(byte[] image) {
		this.image = image;
		return this;
	}

	public Author setImageAttribution(String imageAttribution) {
		this.imageAttribution = imageAttribution;
		return this;
	}

	public Author setLocales(List<Locale> locales) {
		this.locales = locales;
		return this;
	}

	public Author setWikidataId(Integer wikidataId) {
		if (wikidataId == null) {
			return this;
		}

		if (this.wikidataId == null) {
			this.wikidataId = new WikidataId(this);
		}

		this.wikidataId.setWikidataId(wikidataId);
		return this;
	}

	public Author addWork(Work work) {
		if (this.works == null) {
			this.works = new ArrayList<Work>();
		}

		this.works.add(work.setAuthor(this));
		return this;
	}

	public Author addLocale(Locale locale) throws Exception {
		if (this.locales == null) {
			this.locales = new ArrayList<Locale>();
		}

		if (!getMappedLocales().containsKey(locale.getLanguage())) {
			this.locales.add(locale.setAuthor(this));
		}

		return this;
	}

}
