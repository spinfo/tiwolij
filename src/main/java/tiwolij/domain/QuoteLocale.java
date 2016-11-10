package tiwolij.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "quotes_locales")
public class QuoteLocale extends BaseEntity {

	@Id
	@GeneratedValue
	protected Integer id;

	@ManyToOne
	@JoinColumn(name = "quote_id", nullable = false)
	protected Quote quote;

	@Column(nullable = false)
	protected String language;

	@Column(nullable = false)
	protected String schedule;

	@Column(nullable = false, columnDefinition = "TEXT")
	protected String corpus;

	protected String href;

	protected String meta;

	protected Integer year;

	protected String time;

	public QuoteLocale() {
	}

	public QuoteLocale(Quote quote) {
		this.quote = quote;
	}

	public Integer getId() {
		return id;
	}

	public Quote getQuote() {
		return quote;
	}

	public String getLanguage() {
		return language;
	}

	public String getSchedule() {
		return schedule;
	}

	public String getCorpus() {
		return corpus;
	}

	public String getHref() {
		return href;
	}

	public String getMeta() {
		return meta;
	}

	public Integer getYear() {
		return year;
	}

	public String getTime() {
		return time;
	}

	public QuoteLocale setId(Integer id) {
		this.id = id;
		return this;
	}

	public QuoteLocale setQuote(Quote quote) {
		this.quote = quote;
		return this;
	}

	public QuoteLocale setLanguage(String language) {
		this.language = language;
		return this;
	}

	public QuoteLocale setSchedule(String schedule) {
		this.schedule = schedule;
		return this;
	}

	public QuoteLocale setCorpus(String corpus) {
		this.corpus = corpus;
		return this;
	}

	public QuoteLocale setHref(String href) {
		this.href = href;
		return this;
	}

	public QuoteLocale setMeta(String meta) {
		this.meta = meta;
		return this;
	}

	public QuoteLocale setYear(Integer year) {
		this.year = year;
		return this;
	}

	public QuoteLocale setTime(String time) {
		this.time = time;
		return this;
	}
}
