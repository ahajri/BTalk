package com.ahajri.btalk.data.domain.json;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.DynaBean;

import com.ahajri.btalk.data.domain.mapper.JsonDynaMapper;
import com.google.gson.Gson;

/**
 * Generic JAVA Model for content of web service
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 *
 */
public class JsonAction implements Serializable {

	private transient Gson gson = new Gson();
	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -5292129591070639119L;

	private String actionName;

	private DynaBean data;

	private String document;

	private Map<String, String> metadata=new HashMap<String,String>();

	public JsonAction() {
		super();
	}

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
	}

	public DynaBean getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = JsonDynaMapper.mapDynaBean(gson.toJson(data));
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionName == null) ? 0 : actionName.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((document == null) ? 0 : document.hashCode());
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
		JsonAction other = (JsonAction) obj;
		if (actionName != other.actionName)
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (document == null) {
			if (other.document != null)
				return false;
		} else if (!document.equals(other.document))
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
		return "JsonAction [actionName=" + actionName + ", data=" + data + ", document=" + document + ", metadata="
				+ metadata + "]";
	}

}
