package com.ahajri.btalk.data.domain.xml;

import java.io.Serializable;

public class XmlData implements Serializable {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = -9195800927556635132L;

	String xml;

	public XmlData() {

	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

}
