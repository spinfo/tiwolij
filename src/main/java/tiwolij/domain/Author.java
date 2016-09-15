package tiwolij.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "authors")
public class Author {

	@Id
	@GeneratedValue
	private int id;

	@Column(nullable = false)
	private String wikidata_id;

	@Column(nullable = false)
	private String slug;

	private String picture;

	@OneToMany(mappedBy = "author")
	private List<Work> works;

	@OneToMany(mappedBy = "author")
	private List<AuthorLocale> locales;

	public Author() {
	}

	public Author(String wikidata_id) {
		this.wikidata_id = wikidata_id;
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

	public String getWikidata_id() {
		return wikidata_id;
	}

	public String getSlug() {
		return slug;
	}

	public String getPicture() {
		return picture;
	}

	public List<Work> getWorks() {
		return works;
	}

	public List<AuthorLocale> getLocales() {
		return locales;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setWikidata_id(String wikidata_id) {
		this.wikidata_id = wikidata_id;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public void setWorks(List<Work> works) {
		this.works = works;
	}

	public void setLocales(List<AuthorLocale> locales) {
		this.locales = locales;
	}

}
