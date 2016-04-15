package com.ahajri.btalk.data.repository;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahajri.btalk.data.domain.converter.MapEntryConverter;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.sun.media.jfxmedia.track.Track.Encoding;
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
		return toSearchResult(results);
	}

	private List<XmlMap> toSearchResult(SearchHandle resultsHandle) throws IOException {
		List<XmlMap> models = new ArrayList<XmlMap>();
		for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
			System.out.println("  * found : " + summary.getUri());
			// Assumption: summary URI refers to JSON document
			InputStreamHandle handle = new InputStreamHandle();
			xmlDocumentManager.read(summary.getUri(), handle);
			models.add(fetchXmlData(handle));
		}
		return models;
	}

	private XmlMap fetchXmlData(InputStreamHandle handle) throws IOException {

		InputStream is = handle.get();
		String xml = IOUtils.toString(is, Charset.defaultCharset());
		XStream magicApi = new XStream();
		magicApi.registerConverter(new MapEntryConverter());
		magicApi.alias("discussion", XmlMap.class);
		XmlMap xmlMap = (XmlMap) magicApi.fromXML(xml);
		return xmlMap;

	}
}
