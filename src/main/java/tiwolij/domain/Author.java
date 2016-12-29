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
import javax.persistence.Table;

@Entity
@Table(name = "authors")
public class Author extends BaseEntity {

	@Id
	@GeneratedValue
	protected Integer id;

	@Lob
	protected byte[] image;

	protected String imageAttribution = "Unspecified";

	@OneToMany(mappedBy = "author")
	protected List<AuthorLocale> locales = new ArrayList<AuthorLocale>();

	@Column(nullable = false)
	protected String slug;

	protected Integer wikidataId = null;

	@OneToMany(mappedBy = "author")
	protected List<Work> works = new ArrayList<Work>();

	public Author() {
	}

	public Integer getId() {
		return id;
	}

	public byte[] getImage() {
		return image;
	}

	public String getImageAttribution() {
		return imageAttribution;
	}

	public Map<String, AuthorLocale> getLocales() {
		return locales != null ? locales.stream().collect(Collectors.toMap(AuthorLocale::getLanguage, l -> l)) : null;
	}

	public String getSlug() {
		return slug;
	}

	public Integer getWikidataId() {
		return wikidataId;
	}

	public List<Work> getWorks() {
		return works;
	}

	public Author setId(Integer id) {
		this.id = id;
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

	public Author setLocales(List<AuthorLocale> locales) {
		this.locales = locales;
		return this;
	}

	public Author setSlug(String slug) {
		this.slug = slug;
		return this;
	}

	public Author setWikidataId(Integer wikidataId) {
		this.wikidataId = wikidataId;
		return this;
	}

	public Author setWorks(List<Work> works) {
		this.works = works;
		return this;
	}

}
