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

	@Column(nullable = false, columnDefinition = "TEXT")
	protected String corpus;

	protected String curator;

	protected String href;

	@Id
	@GeneratedValue
	protected Integer id;

	@Column(nullable = false)
	protected String language;

	protected Boolean locked = false;

	protected String meta;

	@ManyToOne
	@JoinColumn(name = "quote_id", nullable = false)
	protected Quote quote;

	@Column(nullable = false)
	protected String schedule;

	protected String time;

	protected String year;

	public QuoteLocale() {
	}

	public QuoteLocale(Quote quote) {
		this.quote = quote;
	}

	public String getCorpus() {
		return corpus;
	}

	public String getCurator() {
		return curator;
	}

	public String getDay() {
		return schedule.split("-")[0];
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

	public Boolean getLocked() {
		return locked == null ? false : locked;
	}

	public String getMeta() {
		return meta;
	}

	public String getMonth() {
		return schedule.split("-")[1];
	}

	public Quote getQuote() {
		return quote;
	}

	public String getSchedule() {
		return schedule;
	}

	public String getTime() {
		return time;
	}

	public String getYear() {
		return year;
	}

	public QuoteLocale setCorpus(String corpus) {
		this.corpus = corpus;
		return this;
	}

	public QuoteLocale setCurator(String curator) {
		this.curator = curator;
		return this;
	}

	public QuoteLocale setHref(String href) {
		this.href = href;
		return this;
	}

	public QuoteLocale setId(Integer id) {
		this.id = id;
		return this;
	}

	public QuoteLocale setLanguage(String language) {
		this.language = language;
		return this;
	}

	public QuoteLocale setLocked(Boolean locked) {
		this.locked = locked;
		return this;
	}

	public QuoteLocale setMeta(String meta) {
		this.meta = meta;
		return this;
	}

	public QuoteLocale setQuote(Quote quote) {
		this.quote = quote;
		return this;
	}

	public QuoteLocale setSchedule(String schedule) {
		this.schedule = schedule;
		return this;
	}

	public QuoteLocale setTime(String time) {
		this.time = time;
		return this;
	}

	public QuoteLocale setYear(String year) {
		this.year = year;
		return this;
	}

}
