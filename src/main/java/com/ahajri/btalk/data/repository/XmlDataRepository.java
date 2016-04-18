package com.ahajri.btalk.data.repository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ahajri.btalk.data.domain.converter.MapEntryConverter;
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.ahajri.btalk.utils.ConversionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.ResourceNotFoundException;
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
	private ApplicationContext applicationContext;

	@Value("${marklogic.host}")
	public String host;

	// @Value("${marklogic.port}")
	public String port = "8000";

	@Value("${marklogic.username}")
	public String username;

	@Value("${marklogic.password}")
	public String password;

	@Value("${marklogic.db}")
	public String db;

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
		DatabaseClient databaseClient = getDatabaseClient();
		try {
			XMLDocumentManager xmlDocumentManager = databaseClient.newXMLDocumentManager();
			InputStreamHandle writeHandle = new InputStreamHandle(new DataByteArrayInputStream(xml.getBytes()));
			xmlDocumentManager.write(docPath, metadata, writeHandle);
			databaseClient.release();
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (ForbiddenUserException e) {
			e.printStackTrace();
			throw e;
		} catch (FailedRequestException e) {
			e.printStackTrace();
			throw e;
		} finally {
			databaseClient.release();
		}
	}

	private DatabaseClient getDatabaseClient() {
		return DatabaseClientFactory.newClient(host, Integer.parseInt(port.trim()), db, username, password,
				DatabaseClientFactory.Authentication.DIGEST);
	}

	public List<String> searchDocument(String criteria) throws IOException {
		DatabaseClient databaseClient = getDatabaseClient();
		QueryManager queryManager = databaseClient.newQueryManager();
		StringQueryDefinition qd = queryManager.newStringDefinition();
		// Example criteria : Batman AND Robin
		qd.setCriteria(criteria);
		SearchHandle results = queryManager.search(qd, new SearchHandle());
		// DOMHandle results = queryManager.search(qd, new DOMHandle());
		// StringHandle results = queryManager.search(qd, new
		// StringHandle().withFormat(Format.XML));
		List<String> result = toSearchResult(results, "discussion");
		databaseClient.release();
		return result;
	}

	private List<String> toSearchResult(SearchHandle resultsHandle, String rootName) throws IOException {
		DatabaseClient databaseClient = getDatabaseClient();
		try {
			List<String> jsons = new ArrayList<String>();

			for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
				System.out.println("  * found : " + summary.getUri());
				// Assumption: summary URI refers to JSON document
				InputStreamHandle handle = new InputStreamHandle();
				XMLDocumentManager xmlDocumentManager = databaseClient.newXMLDocumentManager();
				InputStreamHandle xml = xmlDocumentManager.read(summary.getUri(), handle);
				jsons.add(ConversionUtils.xml2Json(IOUtils.toString(xml.get(), Charset.defaultCharset())));
			}

			return jsons;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			databaseClient.release();
		}
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
	public List<String> searchByKeyValue(SearchCriteria criteria, List<String> discussCollections,
			DocumentMetadataHandle metadata, String rootElement) throws IOException {
		DatabaseClient databaseClient = getDatabaseClient();
		QueryManager queryManager = databaseClient.newQueryManager();
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
		List<String> result = toSearchResult(results, rootElement);
		databaseClient.release();
		return result;
	}

	/**
	 * Read document content
	 * 
	 * @param docId:
	 *            document ID
	 * @return {@link Document}
	 */
	public Document readDocumentContent(String docId) {
		DatabaseClient databaseClient = getDatabaseClient();
		try {
			XMLDocumentManager xmlDocumentManager = databaseClient.newXMLDocumentManager();
			DOMHandle handle = new DOMHandle();
			DOMHandle dh = xmlDocumentManager.read(docId, handle);
			Document document = dh.get();
			return document;
		} catch (ResourceNotFoundException e) {
			e.printStackTrace();
			throw e;
		} catch (ForbiddenUserException e) {
			e.printStackTrace();
			throw e;
		} catch (FailedRequestException e) {
			e.printStackTrace();
			throw e;
		} finally {
			databaseClient.release();
		}
	}

	public String readBinaryContent(String docID) throws IOException {
		DatabaseClient databaseClient = getDatabaseClient();
		try {
			XMLDocumentManager xmlDocumentManager = databaseClient.newXMLDocumentManager();
			InputStream is = xmlDocumentManager.read(docID, new InputStreamHandle()).get();

			String xml = IOUtils.toString(is, Charset.defaultCharset());
			System.out.println(xml);
			return xml;
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} finally {
			databaseClient.release();
		}
	}

	public static Map<String, String> convertNodesFromXml(String xml) throws Exception {

		InputStream is = new ByteArrayInputStream(xml.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(is);
		NodeList nodeList = document.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node textChild = nodeList.item(i);
			NodeList childNodes = textChild.getChildNodes();
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node grantChild = childNodes.item(j);
				NodeList grantChildNodes = grantChild.getChildNodes();
				for (int k = 0; k < grantChildNodes.getLength(); k++) {
					if (!org.apache.commons.lang3.StringUtils.isEmpty(grantChildNodes.item(k).getTextContent())) {
						Map<String, String> map = new HashMap<String, String>();
						map.put(grantChildNodes.item(k).getNodeName(), grantChildNodes.item(k).getTextContent());
						System.out.println("##############" + map);
					}
				}
			}
		}
		return createMap(document.getDocumentElement());
	}

	public static Map<String, String> createMap(Node node) {
		Map<String, String> map = new HashMap<String, String>();
		NodeList nodeList = node.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node currentNode = nodeList.item(i);
			if (currentNode.hasAttributes()) {
				for (int j = 0; j < currentNode.getAttributes().getLength(); j++) {
					Node item = currentNode.getAttributes().item(i);
					map.put(item.getNodeName(), item.getTextContent());
				}
			}
			if (node.getFirstChild() != null && node.getFirstChild().getNodeType() == Node.ELEMENT_NODE) {
				map.putAll(createMap(currentNode));
			} else if (node.getFirstChild().getNodeType() == Node.TEXT_NODE) {
				map.put(node.getLocalName(), node.getTextContent());
			}
		}
		return map;
	}
}
