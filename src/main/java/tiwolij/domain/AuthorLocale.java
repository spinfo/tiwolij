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
	protected Integer id;

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

	public Integer getId() {
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

	public AuthorLocale setId(int id) {
		this.id = id;
		return this;
	}

	public AuthorLocale setAuthor(Author author) {
		this.author = author;
		return this;
	}

	public AuthorLocale setLanguage(String language) {
		this.language = language;
		return this;
	}

	public AuthorLocale setName(String name) {
		this.name = name;
		return this;
	}

	public AuthorLocale setHref(String href) {
		this.href = href;
		return this;
	}

}
