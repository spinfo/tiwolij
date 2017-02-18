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
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import tiwolij.util.StringEncoding;

@Entity
@Table(name = "authors")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Author.class)
public class Author implements Serializable {

	private static final long serialVersionUID = -5590869234980268995L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String slug;

	@Lob
	private byte[] image;

	private String imageAttribution;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	@JsonBackReference(value = "works")
	private List<Work> works;

	@OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
	private List<Locale> locales;

	@OneToOne(mappedBy = "author", cascade = CascadeType.ALL)
	private WikidataId wikidataId;

	public Author() {
	}

	public Author merge(Author author) {
		setId(ObjectUtils.firstNonNull(author.getId(), getId()));
		setSlug(ObjectUtils.firstNonNull(author.getSlug(), getSlug()));
		setImage(ObjectUtils.firstNonNull(author.getImage(), getImage()));
		setImageAttribution(ObjectUtils.firstNonNull(author.getImageAttribution(), getImageAttribution()));
		setWorks(ObjectUtils.firstNonNull(author.getWorks(), getWorks()));
		setLocales(ObjectUtils.firstNonNull(author.getLocales(), getLocales()));
		setWikidataId(ObjectUtils.firstNonNull(author.getWikidataId(), getWikidataId()));

		return this;
	}

	public Boolean hasId() {
		return (id != null && id > 0);
	}

	public Boolean hasSlug() {
		return (slug != null && !slug.isEmpty());
	}

	public Boolean hasImage() {
		return (image != null && image != new byte[0] && image.length > 0);
	}

	public Boolean hasImageAttribution() {
		return (imageAttribution != null && !imageAttribution.isEmpty());
	}

	public Boolean hasWorks() {
		return (works != null && !works.isEmpty());
	}

	public Boolean hasQuotes() {
		return (getQuotes() != null && !getQuotes().isEmpty());
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

	public byte[] getImage() {
		return image;
	}

	public String getImageAttribution() {
		return imageAttribution;
	}

	public List<Work> getWorks() {
		return works;
	}

	@JsonIgnore
	public List<Quote> getQuotes() {
		return works.stream().map(i -> i.getQuotes()).flatMap(List::stream).collect(Collectors.toList());
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

	public Author setWorks(List<Work> works) {
		this.works = works;
		return this;
	}

	public Author setLocales(List<Locale> locales) {
		this.locales = locales;
		return this;
	}

	public Author setWikidataId(Integer wikidataId) {
		if (wikidataId != null) {
			if (this.wikidataId == null) {
				this.wikidataId = new WikidataId(this);
			}

			this.wikidataId.setWikidataId(wikidataId);
		}

		return this;
	}

	public Author addWork(Work work) {
		if (work != null) {
			if (this.works == null) {
				this.works = new ArrayList<Work>();
			}

			this.works.add(work.setAuthor(this));
		}

		return this;
	}

	public Author addLocale(Locale locale) {
		if (locale != null) {
			if (this.locales == null) {
				this.locales = new ArrayList<Locale>();
			}

			if (!hasLocale(locale.getLanguage())) {
				this.locales.add(locale.setAuthor(this));
			}
		}

		return this;
	}

}
