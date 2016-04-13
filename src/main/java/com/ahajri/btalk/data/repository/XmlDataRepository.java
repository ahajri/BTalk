package com.ahajri.btalk.data.repository;

import org.apache.commons.beanutils.DynaBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.QueryManager;

@Component("xmlDataRepository")
public class XmlDataRepository {

	@Autowired
	protected QueryManager queryManager;

	@Autowired
	protected XMLDocumentManager xmlDocumentManager;

	/**
	 * Repository Method to create Document on MarkLogic Database
	 * 
	 * @param xml:
	 *            XML Data
	 * @param metadata
	 *            {@link DocumentMetadataHandle}
	 * @param model:
	 *            {@link DynaBean} of XML Data
	 * @param DIR:
	 *            Directory to store document
	 */
	public void persist(String xml, DocumentMetadataHandle metadata, DynaBean model, String DIR) {
		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(model, JsonNode.class);
		writeHandle.set(writeDocument);
		StringHandle stringHandle = new StringHandle(writeDocument.toString());
		xmlDocumentManager.write(DIR + model.get("document").toString(), metadata, stringHandle);
	}

}
