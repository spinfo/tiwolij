package tiwolij.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quote extends BaseEntity {

	@Id
	@GeneratedValue
	protected Integer id;

	@OneToMany(mappedBy = "quote")
	protected List<QuoteLocale> locales;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	protected Work work;

	public Quote() {
	}

	public Quote(Work work) {
		this.work = work;
	}

	public Integer getId() {
		return id;
	}

	public Map<String, QuoteLocale> getLocales() {
		return locales != null ? locales.stream().collect(Collectors.toMap(QuoteLocale::getLanguage, l -> l)) : null;
	}

	public Work getWork() {
		return work;
	}

	public Quote setId(Integer id) {
		this.id = id;
		return this;
	}

	public Quote setLocales(List<QuoteLocale> locales) {
		this.locales = locales;
		return this;
	}

	public Quote setWork(Work work) {
		this.work = work;
		return this;
	}

}
