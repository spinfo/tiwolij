package tiwolij.domain;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quote {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	private Work work;

	@Column(nullable = false)
	private Date schedule;

	@OneToMany(mappedBy = "quote")
	private List<QuoteLocale> locales;

	public Quote() {
	}

	public Quote(Work work) {
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

	public Date getSchedule() {
		return schedule;
	}

	public List<QuoteLocale> getLocales() {
		return locales;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setWork(Work work) {
		this.work = work;
	}

	public void setSchedule(Date schedule) {
		this.schedule = schedule;
	}

	public void setLocales(List<QuoteLocale> locales) {
		this.locales = locales;
	}
}
