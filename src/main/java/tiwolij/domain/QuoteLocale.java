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
public class QuoteLocale {

	@Id
	@GeneratedValue
	private int id;

	@ManyToOne
	@JoinColumn(name = "quote_id", nullable = false)
	private Quote quote;

	@Column(nullable = false)
	private String language;

	@Column(nullable = false)
	private String corpus;

	private String href;

	private String meta;

	public QuoteLocale() {
	}

	public QuoteLocale(Quote quote) {
		this.quote = quote;
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

	public Quote getQuote() {
		return quote;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setQuote(Quote quote) {
		this.quote = quote;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setCorpus(String corpus) {
		this.corpus = corpus;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public void setMeta(String meta) {
		this.meta = meta;
	}

}
