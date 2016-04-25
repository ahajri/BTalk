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
import org.apache.log4j.Logger;
import org.fusesource.hawtbuf.DataByteArrayInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ahajri.btalk.data.domain.converter.MapEntryConverter;
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.utils.ConversionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hp.hpl.jena.sparql.pfunction.library.alt;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.FailedRequestException;
import com.marklogic.client.ForbiddenUserException;
import com.marklogic.client.MarkLogicIOException;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.Transaction;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.PathLanguage;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.DeleteQueryDefinition;
import com.marklogic.client.query.KeyValueQueryDefinition;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.persistence.XmlMap;

@Component("xmlDataRepository")
public class XmlDataRepository {

	/** LOGGER */
	private static final Logger LOGGER = Logger.getLogger(XmlDataRepository.class);

	@Value("${marklogic.host}")
	public String host;

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
			LOGGER.error(e);
			throw e;
		} catch (ForbiddenUserException e) {
			LOGGER.error(e);
			throw e;
		} catch (FailedRequestException e) {
			LOGGER.error(e);
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
				LOGGER.info(" URI * found : " + summary.getUri());
				InputStreamHandle handle = new InputStreamHandle();
				XMLDocumentManager xmlDocumentManager = databaseClient.newXMLDocumentManager();
				InputStreamHandle xml = xmlDocumentManager.read(summary.getUri(), handle);
				jsons.add(ConversionUtils.xml2Json(IOUtils.toString(xml.get(), Charset.defaultCharset())));
			}
			return jsons;
		} catch (Exception e) {
			LOGGER.error(e);
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
	@SuppressWarnings({ "rawtypes", "unused" })
	private Map fetchXmlData(InputStreamHandle handle, String rootName, InputStreamHandle xml) throws IOException {
		XStream xStream = new XStream();
		xStream.registerConverter(new MapEntryConverter());
		xStream.alias(rootName, HashMap.class);
		return (Map) xStream.fromXML(ConversionUtils.getXml(xml));
	}

	/**
	 * Search By Key Value
	 * 
	 * @param criteria:
	 *            {@link SearchCriteria}
	 * 
	 * @param discussCollections:
	 *            List of collection names
	 * 
	 * @param metadata
	 *            {@link DocumentMetadataHandle}
	 * 
	 * @return List of XML content of found documents
	 * 
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
			LOGGER.error(e);
			throw e;
		} catch (ForbiddenUserException e) {
			LOGGER.error(e);
			throw e;
		} catch (FailedRequestException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			databaseClient.release();
		}
	}

	/**
	 * 
	 * @param criteria:
	 *            {@link SearchCriteria}
	 * @param DIR:
	 *            directory to delete
	 * @return {@link Boolean} while the directory was deleted or not
	 */
	public boolean deleteDirectory(SearchCriteria criteria, String DIR) {
		boolean isDeleted = false;
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		try {
			QueryManager queryManager = databaseClient.newQueryManager();
			DeleteQueryDefinition dqDef = queryManager.newDeleteDefinition();
			dqDef.setDirectory(DIR);
			queryManager.delete(dqDef, transaction);
			transaction.commit();
			isDeleted = true;
		} catch (ForbiddenUserException e) {
			transaction.rollback();
			LOGGER.error(e);
		} catch (FailedRequestException e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isDeleted;
	}

	/**
	 * Delete document with its URI
	 * 
	 * @param docURIs:
	 *            Document URI
	 * @return True if document deleted, false if not
	 */
	public boolean deleteDocument(String[] docURIs) {
		boolean isDeleted = false;
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			documentManager.delete(transaction, docURIs);
			transaction.commit();
			isDeleted = true;
		} catch (Exception e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isDeleted;
	}

	/**
	 * 
	 * @param docID:
	 *            Document ID
	 * @param fragment:
	 *            XML fragment to add
	 * @return: {@link Boolean}while fragment inserted or not
	 */
	public boolean patchDocument(String docID, String xmlFragment, String tag) {
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		boolean isPatched = false;
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			DocumentPatchBuilder dpBuilder = documentManager.newPatchBuilder();
			dpBuilder.insertFragment(tag, Position.LAST_CHILD, xmlFragment);
			DocumentPatchHandle handle = dpBuilder.build();
			documentManager.patch(docID, handle, transaction);
			transaction.commit();
			isPatched = true;
		} catch (ForbiddenUserException e) {
			transaction.rollback();
			LOGGER.error(e);
		} catch (FailedRequestException e) {
			transaction.rollback();
			LOGGER.error(e);
		} catch (MarkLogicIOException e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isPatched;
	}

	/**
	 * Delete Tag
	 * 
	 * @param docID:
	 *            Document ID
	 * @param path:
	 *            Tag path <code>Example: /root/child</code>
	 * @return <code>true</code>if Tag was deleted, <code>false</code>if not
	 */
	public boolean deleteTag(String docID, String path) {
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		boolean isPatched = false;
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			DocumentPatchBuilder dpBuilder = documentManager.newPatchBuilder();
			dpBuilder.pathLanguage(PathLanguage.XPATH);
			dpBuilder.replaceFragment(path, null);
			DocumentPatchHandle handle = dpBuilder.build();
			documentManager.patch(docID, handle, transaction);
			transaction.commit();
			isPatched = true;
		} catch (Exception e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isPatched;
	}

	/**
	 * Replace XML Tag Value
	 * 
	 * @param docID:
	 *            Document ID
	 * 
	 * @param path:
	 *            Tag path
	 * 
	 * @param xmlFragment:
	 *            Value to change
	 * 
	 * @return <code>true</code> IF value replaced else <code>false</code>
	 */
	public boolean replaceTagValue(String docID, String path, String xmlFragment) {
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		boolean isPatched = false;
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			DocumentPatchBuilder dpBuilder = documentManager.newPatchBuilder();
			dpBuilder.pathLanguage(PathLanguage.XPATH);
			dpBuilder.replaceValue(path, xmlFragment);
			DocumentPatchHandle handle = dpBuilder.build();
			documentManager.patch(docID, handle, transaction);
			transaction.commit();
			isPatched = true;
		} catch (Exception e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isPatched;
	}

	/**
	 * Insert new Tag in given Path
	 * 
	 * @param docID:
	 *            document ID
	 * @param path:
	 *            Tag Path
	 * @param newTagName:
	 *            new Tag name
	 * @param xmlValue:
	 *            New Tag Value
	 * @return <code>true</code> IF value inserted else <code>false</code>
	 */
	public boolean insertTag(String docID, String path, String newTagName, String xmlValue) {
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		boolean isPatched = false;
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			DocumentPatchBuilder dpBuilder = documentManager.newPatchBuilder();
			dpBuilder.pathLanguage(PathLanguage.XPATH);
			ObjectMapper mapper = new ObjectMapper();
			dpBuilder.replaceValue(path, mapper.createObjectNode().put(newTagName, xmlValue));
			DocumentPatchHandle handle = dpBuilder.build();
			documentManager.patch(docID, handle, transaction);
			transaction.commit();
			isPatched = true;
		} catch (Exception e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isPatched;
	}

	/**
	 * insert an array in given path in XML document
	 * 
	 * @param docID:
	 *            document ID
	 * @param path:
	 *            tag array path, example
	 *            <code>/parent/array-node('child')</code>
	 * @param newTagName:
	 *            new Tag name
	 * @param xmlValues:
	 *            Array of XML tags content
	 * @return <code>true</code> IF values inserted else <code>false</code>
	 */
	public boolean insertTagArray(String docID, String path, List<String> xmlValues) {
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		boolean isPatched = false;
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			DocumentPatchBuilder dpBuilder = documentManager.newPatchBuilder();
			dpBuilder.pathLanguage(PathLanguage.XPATH);
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode array = mapper.createArrayNode();
			for (int i = 0; i < xmlValues.size(); i++) {
				array.add(xmlValues.get(i));
			}
			dpBuilder.replaceFragment(path, array);
			DocumentPatchHandle handle = dpBuilder.build();
			documentManager.patch(docID, handle, transaction);
			transaction.commit();
			isPatched = true;
		} catch (Exception e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			databaseClient.release();
		}
		return isPatched;
	}

	/**
	 * Update XML node value
	 * 
	 * @param docID:
	 *            document ID
	 * @param xmlFragment:
	 *            XML to update
	 * @param path:
	 *            path to tag
	 * @return {@link Boolean} while fragment updated or not
	 */
	public boolean replacePatch(String docID, String xmlFragment, String path) {
		DatabaseClient databaseClient = getDatabaseClient();
		Transaction transaction = databaseClient.openTransaction();
		boolean isPatched = false;
		try {
			XMLDocumentManager documentManager = databaseClient.newXMLDocumentManager();
			DocumentPatchBuilder dpBuilder = documentManager.newPatchBuilder();
			dpBuilder.pathLanguage(PathLanguage.XPATH);
			dpBuilder.replaceFragment(path, xmlFragment);
			DocumentPatchHandle handle = dpBuilder.build();
			documentManager.patch(docID, handle, transaction);
		} catch (Exception e) {
			transaction.rollback();
			LOGGER.error(e);
		} finally {
			transaction.commit();
			databaseClient.release();
			isPatched = true;
		}
		return isPatched;
	}

	/**
	 * Read Binary Content
	 * 
	 * @param docID:
	 *            ID of document
	 * @return: binary content of Document
	 * @throws IOException
	 */
	public String readBinaryContent(String docID) throws IOException {
		DatabaseClient databaseClient = getDatabaseClient();
		try {
			XMLDocumentManager xmlDocumentManager = databaseClient.newXMLDocumentManager();
			InputStream is = xmlDocumentManager.read(docID, new InputStreamHandle()).get();
			String xml = IOUtils.toString(is, Charset.defaultCharset());
			return xml;
		} catch (IOException e) {
			LOGGER.error(e);
			throw e;
		} finally {
			databaseClient.release();
		}
	}

	@SuppressWarnings("unused")
	private Map<String, String> convertNodesFromXml(String xml) throws Exception {
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
					}
				}
			}
		}
		return createMap(document.getDocumentElement());
	}

	private Map<String, String> createMap(Node node) {
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
