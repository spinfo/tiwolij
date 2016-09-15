package tiwolij.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "authors_locales")
public class AuthorLocale {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	private Author author;

	@Column(nullable = false)
	private String language;

	@Column(nullable = false)
	private String name;

	private String href;

	public AuthorLocale() {
	}

	public AuthorLocale(Author author) {
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

	public String getLanguage() {
		return language;
	}

	public String getName() {
		return name;
	}

	public String getHref() {
		return href;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setAuthor(Author author) {
		this.author = author;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHref(String href) {
		this.href = href;
	}

}
