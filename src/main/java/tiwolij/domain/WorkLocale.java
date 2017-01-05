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

	protected String href;

	@Id
	@GeneratedValue
	protected Integer id;

	@Column(nullable = false)
	protected String language;

	@Column(nullable = false)
	protected String name;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	protected Work work;

	public WorkLocale() {
	}

	public WorkLocale(Work work) {
		this.work = work;
	}

	public String getHref() {
		return href;
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

	public Work getWork() {
		return work;
	}

	public WorkLocale setHref(String href) {
		this.href = href;
		return this;
	}

	public WorkLocale setId(Integer id) {
		this.id = id;
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

	public WorkLocale setWork(Work work) {
		this.work = work;
		return this;
	}

}
