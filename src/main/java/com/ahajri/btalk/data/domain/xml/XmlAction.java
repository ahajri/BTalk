package com.ahajri.btalk.data.domain.xml;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.ahajri.btalk.utils.ActionName;

/**
 * Generic JAVA Model for content of web service
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 *
 */
@XmlRootElement(name = "Discussion")
public class XmlAction implements Serializable {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 4234778075164340991L;

	private ActionName actionName;

	private String data;

	private String document;

	private Map<String, String> metadata;

	public XmlAction() {
		super();
	}

	public ActionName getActionName() {
		return actionName;
	}

	public void setActionName(ActionName actionName) {
		this.actionName = actionName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
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

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
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
		XmlAction other = (XmlAction) obj;
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
		return "XmlAction [actionName=" + actionName + ", data=" + data + ", document=" + document + ", metadata="
				+ metadata + "]";
	}

}
