package tiwolij.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "locales")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id", scope = Locale.class)
public class Locale implements Serializable {

	private static final long serialVersionUID = 6517072584087211218L;

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String language;

	@Column(nullable = false)
	private String name;

	private String href;

	@ManyToOne
	@JoinColumn(name = "author_id")
	private Author author;

	@ManyToOne
	@JoinColumn(name = "work_id")
	private Work work;

	public Locale() {
	}

	public Locale(Author author) {
		this.author = author;
	}

	public Locale(Work work) {
		this.work = work;
	}

	public Integer getId() {
		return id;
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

	public Author getAuthor() {
		return author;
	}

	public Work getWork() {
		return work;
	}

	public Boolean hasId() {
		return (id != null && id > 0);
	}

	public Boolean hasLanguage() {
		return (language != null && !language.isEmpty());
	}

	public Boolean hasName() {
		return (name != null && !name.isEmpty());
	}

	public Boolean hasHref() {
		return (href != null && !href.isEmpty());
	}

	public Locale setId(Integer id) {
		this.id = id;
		return this;
	}

	public Locale setLanguage(String language) {
		this.language = language;
		return this;
	}

	public Locale setName(String name) {
		this.name = name;
		return this;
	}

	public Locale setHref(String href) {
		this.href = href;
		return this;
	}

	public Locale setAuthor(Author author) {
		this.work = null;
		this.author = author;
		return this;
	}

	public Locale setWork(Work work) {
		this.author = null;
		this.work = work;
		return this;
	}

}
