package com.ahajri.btalk.data.domain.xml;

import java.util.HashMap;

public class XmlDoc {

	private String documentId;
	
	private HashMap<String, Object> metadata = new HashMap<String,Object>();

	public XmlDoc() {
		super();
	}

	public String getDocumentId() {
		return documentId;
	}

	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	
	public HashMap<String, Object> getMetadata() {
		return metadata;
	}
	public void setMetadata(HashMap<String, Object> metadata) {
		this.metadata = metadata;
	}
}
