package tiwolij.service.tsv;

import java.util.List;
import java.util.Map;

import tiwolij.domain.Quote;

public interface TSVService {

	public Map<Integer, Quote> getResults();

	public Map<Integer, Exception> getErrors();

	public void process(String encoding, List<String> fields, String language, byte[] tsv) throws Exception;

}
