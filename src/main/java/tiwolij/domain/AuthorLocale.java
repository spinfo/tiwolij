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
public class AuthorLocale extends BaseEntity {

	@Id
	@GeneratedValue
	protected int id;

	@ManyToOne
	@JoinColumn(name = "author_id", nullable = false)
	protected Author author;

	@Column(nullable = false)
	protected String language;

	@Column(nullable = false)
	protected String name;

	protected String href;

	public AuthorLocale() {
	}

	public AuthorLocale(Author author) {
		this.author = author;
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
