package com.ahajri.btalk.data.domain.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ahajri.btalk.data.domain.mapper.DataMapper;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.google.gson.Gson;

/**
 * Generic JAVA Model for content of web service
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 */
@XmlRootElement(name = "action")
public class WebAction implements Serializable {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -5292129591070639119L;
	//
	private final Gson gson = new Gson();
	//
	private String actionName;
	private String jsonData;
	private String document;

	private Map<String, String> metadata = new HashMap<String, String>();

	public WebAction() {
		super();
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> map) {
		this.metadata = map;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionName == null) ? 0 : actionName.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((jsonData == null) ? 0 : jsonData.hashCode());
		result = prime * result + ((metadata == null) ? 0 : metadata.hashCode());
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
		WebAction other = (WebAction) obj;
		if (actionName == null) {
			if (other.actionName != null)
				return false;
		} else if (!actionName.equals(other.actionName))
			return false;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
			return false;
		if (jsonData == null) {
			if (other.jsonData != null)
				return false;
		} else if (!jsonData.equals(other.jsonData))
			return false;
		if (metadata == null) {
			if (other.metadata != null)
				return false;
		} else if (!metadata.equals(other.metadata))
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "WebAction [actionName=" + actionName + ", jsonData=" + jsonData + ", document="
				+ document + ", metadata=" + metadata + "]";
	}

}
