package tiwolij.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "works_locales")
public class WorkLocale extends BaseEntity {

	@Id
	@GeneratedValue
	protected Integer id;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	protected Work work;

	@Column(nullable = false)
	protected String language;

	@Column(nullable = false)
	protected String name;

	protected String href;

	public WorkLocale() {
	}

	public WorkLocale(Work work) {
		this.work = work;
	}

	public Integer getId() {
		return id;
	}

	public Work getWork() {
		return work;
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

	public WorkLocale setId(Integer id) {
		this.id = id;
		return this;
	}

	public WorkLocale setWork(Work work) {
		this.work = work;
		return this;
	}

	public WorkLocale setLanguage(String language) {
		this.language = language;
		return this;
	}

	public WorkLocale setName(String name) {
		this.name = name;
		return this;
	}

	public WorkLocale setHref(String href) {
		this.href = href;
		return this;
	}

}
