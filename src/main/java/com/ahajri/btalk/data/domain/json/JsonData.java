package com.ahajri.btalk.data.domain.json;

/**
 * JAVA Model for JSON data provided by the web client
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 */
public class JsonData {

	private String json;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((json == null) ? 0 : json.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonData other = (JsonData) obj;
		if (json == null) {
			if (other.json != null)
				return false;
		} else if (!json.equals(other.json))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "JsonData [json=" + json + "]";
	}

}
