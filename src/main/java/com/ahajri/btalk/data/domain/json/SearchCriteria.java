package com.ahajri.btalk.data.domain.json;

import java.io.Serializable;
import java.util.LinkedHashMap;

/**
 * Search Criteria modeling class
 * 
 * @author: 
 *         <p>
 *         ahajri
 *         <P>
 *
 */
public class SearchCriteria implements Serializable {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 8822281893846572929L;

	private String query;
	private LinkedHashMap<String, Object> keyValues;
	private int pageLength;
	private int start;

	public SearchCriteria() {
		super();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getPageLength() {
		return pageLength;
	}

	public void setPageLength(int pageLength) {
		this.pageLength = pageLength;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public LinkedHashMap<String, Object> getKeyValues() {
		return keyValues;
	}

	public void setKeyValues(LinkedHashMap<String, Object> keyValues) {
		this.keyValues = keyValues;
	}

	@Override
	public String toString() {
		return "SearchCriteria [query=" + query + ", keyValues=" + keyValues.toString() + ", pageLength=" + pageLength + ", start="
				+ start + "]";
	}

}
