package com.ahajri.btalk.data.repository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.apache.commons.io.IOUtils;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import com.ahajri.btalk.data.domain.converter.MapEntryConverter;
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.thoughtworks.xstream.XStream;

@Component("xmlDataRepository")
public class XmlDataRepository {

	@Autowired
	protected QueryManager queryManager;

	@Autowired
	protected XMLDocumentManager xmlDocumentManager;

	@Autowired
	protected DatabaseClient databaseClient;

	/**
	 * Create XML document in MarkLogic database
	 * 
	 * @param xml:
	 *            XML document content
	 * @param metadata:
	 *            document metadata
	 * @param docPath:
	 *            document path
	 * @throws IOException
	 * @throws JsonProcessingException
	 */
	public void persist(String xml, DocumentMetadataHandle metadata, String docPath)
			throws JsonProcessingException, IOException {
		InputStreamHandle writeHandle = new InputStreamHandle(new DataByteArrayInputStream(xml.getBytes()));
		xmlDocumentManager.write(docPath, metadata, writeHandle);
		databaseClient.release();
	}

	public List<XmlMap> searchDocument(String criteria) throws IOException {
		StringQueryDefinition qd = queryManager.newStringDefinition();
		// Example criteria : Batman AND Robin
		qd.setCriteria(criteria);
		SearchHandle results = queryManager.search(qd, new SearchHandle());
		// DOMHandle results = queryManager.search(qd, new DOMHandle());
		// StringHandle results = queryManager.search(qd, new
		// StringHandle().withFormat(Format.XML));
		return toSearchResult(results, "discussion");
	}

	private List<XmlMap> toSearchResult(SearchHandle resultsHandle, String rootName) throws IOException {
		List<XmlMap> models = new ArrayList<XmlMap>();
		for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
			System.out.println("  * found : " + summary.getUri());
			// Assumption: summary URI refers to JSON document
			InputStreamHandle handle = new InputStreamHandle();
			InputStreamHandle xml = xmlDocumentManager.read(summary.getUri(), handle);

			models.add(fetchXmlData(handle, rootName, xml));
		}
		return models;
	}

	/**
	 * 
	 * @param handle
	 * @param rootName
	 *            root
	 * @return {@link XmlMap}
	 * @throws IOException
	 */
	private XmlMap fetchXmlData(InputStreamHandle handle, String rootName, InputStreamHandle xml) throws IOException {
		// InputStream is = handle.get();
		// String xml = IOUtils.toString(is, Charset.defaultCharset());
		XStream xStream = new XStream();
		xStream.registerConverter(new MapEntryConverter());
		xStream.alias(rootName, HashMap.class);
		// xStream.aliasType("headers", LinkedHashMap.class);
		// xStream.aliasType("host", String.class);

		HashMap map = (HashMap) xStream.fromXML(IOUtils.toString(xml.get(), Charset.defaultCharset()));
		XmlMap xmlMap = new XmlMap();
		xmlMap.putAll(map);
		return xmlMap;
	}

	/**
	 * 
	 * @param criteria
	 * @param discussCollections
	 * @param metadata
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public List<XmlMap> searchByKeyValue(SearchCriteria criteria, List<String> discussCollections,
			DocumentMetadataHandle metadata) throws IOException {
		KeyValueQueryDefinition kvqdef = queryManager.newKeyValueDefinition();
		LinkedHashMap<String, Object> keyValues = criteria.getKeyValues();
		for (Iterator<Entry<String, Object>> iterator = keyValues.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> entry = iterator.next();
			String key = entry.getKey();
			Object value = entry.getValue();
			String vStr = null;
			if (value != null) {
				vStr = String.valueOf(value);
			}
			kvqdef.put(queryManager.newElementLocator(new QName(key)), String.valueOf(vStr));
		}
		SearchHandle results = queryManager.search(kvqdef, new SearchHandle());
		return toSearchResult(results, "discussion");
	}

	/**
	 * Read document content
	 * @param docId: document ID
	 * @return {@link Document}
	 */
	public Document readDocumentContent(String docId) {
		DOMHandle handle = new DOMHandle();
		xmlDocumentManager.read(docId, handle);
		Document document = handle.get();
		databaseClient.release();
		return document;
	}
}
