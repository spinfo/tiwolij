package tiwolij.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "quotes")
public class Quote {

	@Id
	@GeneratedValue
	private Integer id;

	@Column(nullable = false)
	private String schedule;

	private String year;

	private String time;

	@Column(nullable = false)
	private String language;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String corpus;

	private String href;

	private String meta;

	private String curator;

	@Column(nullable = false)
	private Boolean locked = false;

	@ManyToOne
	@JoinColumn(name = "work_id", nullable = false)
	private Work work;

	public Quote() {
	}

	public Quote(Work work) {
		this.work = work;
	}

	public Integer getId() {
		return id;
	}

	public String getSchedule() {
		return schedule;
	}

	public String getYear() {
		return year;
	}

	public String getMonth() {
		return schedule.split("-")[1];
	}

	public String getDay() {
		return schedule.split("-")[0];
	}

	public String getTime() {
		return time;
	}

	public String getLanguage() {
		return language;
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

	public String getCurator() {
		return curator;
	}

	public Boolean getLocked() {
		return locked;
	}

	public Author getAuthor() {
		return work.getAuthor();
	}

	public Work getWork() {
		return work;
	}

	public Boolean hasId() {
		return (id != null && id > 0);
	}

	public Boolean hasSchedule() {
		return (schedule != null && !schedule.isEmpty());
	}

	public Boolean hasYear() {
		return (year != null && !year.isEmpty());
	}

	public Boolean hasTime() {
		return (time != null && !time.isEmpty());
	}

	public Boolean hasLanguage() {
		return (language != null && !language.isEmpty());
	}

	public Boolean hasCorpus() {
		return (corpus != null && !corpus.isEmpty());
	}

	public Boolean hasHref() {
		return (href != null && !href.isEmpty());
	}

	public Boolean hasMeta() {
		return (meta != null && !meta.isEmpty());
	}

	public Boolean hasCurator() {
		return (curator != null && !curator.isEmpty());
	}

	public Quote setId(Integer id) {
		this.id = id;
		return this;
	}

	public Quote setSchedule(String schedule) {
		this.schedule = schedule;
		return this;
	}

	public Quote setYear(String year) {
		this.year = year;
		return this;
	}

	public Quote setTime(String time) {
		this.time = time;
		return this;
	}

	public Quote setLanguage(String language) {
		this.language = language;
		return this;
	}

	public Quote setCorpus(String corpus) {
		this.corpus = corpus;
		return this;
	}

	public Quote setHref(String href) {
		this.href = href;
		return this;
	}

	public Quote setMeta(String meta) {
		this.meta = meta;
		return this;
	}

	public Quote setCurator(String curator) {
		this.curator = curator;
		return this;
	}

	public Quote setLocked(Boolean locked) {
		this.locked = locked;
		return this;
	}

	public Quote setWork(Work work) {
		this.work = work;
		return this;
	}

}
