package com.ahajri.btalk.data.domain.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

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
	private String actionName;
	private Object jsonData;
	private String document;

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

	public Object getJsonData() {
		return jsonData;
	}

	public void setJsonData(Object jsonData) {
		this.jsonData = jsonData;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionName == null) ? 0 : actionName.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
		result = prime * result + ((jsonData == null) ? 0 : jsonData.hashCode());
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

		return true;
	}

	@Override
	public String toString() {
		return "WebAction [actionName=" + actionName + ", jsonData=" + jsonData + ", document=" + document + "]";
	}

}
