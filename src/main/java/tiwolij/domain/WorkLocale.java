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
public class WorkLocale {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	private Work work;

	@Column(nullable = false)
	private String language;

	@Column(nullable = false)
	private String name;

	private String href;

	public WorkLocale() {
	}

	public WorkLocale(Work work) {
		this.work = work;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setWork(Work work) {
		this.work = work;
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
