package com.ahajri.btalk.data.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.data.domain.json.WebAction;
import com.ahajri.btalk.data.repository.XmlDataRepository;
import com.ahajri.btalk.utils.ActionResultName;
import com.ahajri.btalk.utils.ConversionUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jayway.jsonpath.Criteria;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.thoughtworks.xstream.persistence.XmlMap;

@Service("documentService")
public class DocumentService {

	@Autowired
	private XmlDataRepository xmlDataRepository;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Persist document
	 * 
	 * @param jsonAction:
	 *            {@link WebAction}
	 * 
	 * @param action
	 *            {@link WebAction}
	 * @param collectionNames
	 *            data collections list
	 * @return {@link Boolean} while action was successful or not
	 */
	@SuppressWarnings("rawtypes")
	public ActionResult createDocument(Map action, List<String> collectionNames, String xml) {
		ActionResult result = getCreatedResult();
		result.setJsonReturnData("{\"info\":\" O yeeeeah, Well done man\"}");
		// xmlFormOfBean = sos.toString();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		if (!ListUtils.isEqualList(collectionNames, null)) {
			metadata.getCollections().addAll(collectionNames);
		}
		try {
			xmlDataRepository.persist(xml, metadata, (String) action.get("document"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			result = getErrorResult();
			result.setJsonReturnData(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			result = getErrorResult();
			result.setJsonReturnData(e.getMessage());
		}
		return result;
	}

	/**
	 * Search Document
	 * 
	 * @param criteria:
	 *            {@link SearchCriteria}
	 * @return {@link ActionResult}
	 */
	public ActionResult searchDocument(SearchCriteria criteria) {
		ActionResult result = new ActionResult();
		result.setActionResultName(ActionResultName.SUCCESSFULL);
		result.setStatus(HttpStatus.FOUND);
		try {
			List<String> found = xmlDataRepository.searchDocument(criteria.getQuery());
			result.setJsonReturnData(ConversionUtils.xml2Json(found.toString()));
		} catch (IOException e) {
			result = getErrorResult();
			result.setJsonReturnData(e.getMessage());
		}
		return result;
	}

	/**
	 * 
	 * @return {@link ActionResult}
	 */
	private ActionResult getErrorResult() {
		ActionResult result = new ActionResult();
		result.setActionResultName(ActionResultName.FAIL);
		result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		return result;
	}

	/**
	 * 
	 * @return {@link ActionResult}
	 */
	private ActionResult getCreatedResult() {
		ActionResult result = new ActionResult();
		result.setActionResultName(ActionResultName.SUCCESSFULL);
		result.setStatus(HttpStatus.CREATED);
		return result;
	}

	/**
	 * Search in XML By Key Value
	 * 
	 * @param criteria:
	 *            {@link Criteria}
	 * 
	 * @param discussCollections
	 *            : Collections names
	 * 
	 * @return {@link ActionResult}
	 */
	public ActionResult searchByKeyValue(SearchCriteria criteria, List<String> discussCollections) {
		ActionResult result = new ActionResult();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		if (!ListUtils.isEqualList(discussCollections, null)) {
			metadata.getCollections().addAll(discussCollections);
		}
		List<String> foundData = null;
		try {
			foundData = xmlDataRepository.searchByKeyValue(criteria, discussCollections, metadata, "discussions");
		} catch (IOException e) {
			result.setActionResultName(ActionResultName.FAIL);
			result.setStatus(HttpStatus.NOT_FOUND);
			result.setJsonReturnData(e.getMessage());
		}
		result.setStatus(HttpStatus.FOUND);
		result.setActionResultName(ActionResultName.SUCCESSFULL);
		result.setJsonReturnData(foundData);
		return result;
	}

	/**
	 * Patch Fragment
	 * 
	 * @param docID:
	 *            Document ID
	 * @param fragment:
	 *            XML fragment to add
	 * @return {@link ActionResult}
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ActionResult patchFragment(Map map) {
		String docID = (String) map.get("docID");
		String fragment = (String) map.get("fragment");
		String tag = (String) map.get("tag");

		Map senderID = (Map) map.get("senderID");

		ActionResult result = new ActionResult();
		result.setJsonReturnData(docID);
		Map m = new HashMap<>();
		m.put("text", fragment);
		m.put("senderID", senderID);
		m.put("datetime", sdf.format(new Date()));
		m.put("acquitted", false);
		String xmlFragment = ConversionUtils.getXml(m, "message");
		boolean isPathced = xmlDataRepository.patchDocument(docID, xmlFragment, tag);
		if (isPathced) {
			result.setStatus(HttpStatus.OK);
			result.setActionResultName(ActionResultName.SUCCESSFULL);
		} else {
			result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			result.setActionResultName(ActionResultName.FAIL);
			result.getMessages().add("Patching fragment fails on " + docID);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ActionResult updateFragmentValue(Map map) {
		String docID = (String) map.get("docID");
		String fragment = (String) map.get("fragment");
		String path = (String) map.get("path");
		Map senderID = (Map) map.get("senderID");
		boolean acquitted = (Boolean) map.get("acquitted");
		ActionResult result = new ActionResult();
		result.setJsonReturnData(docID);
		Map m = new HashMap<>();
		m.put("text", fragment);
		m.put("senderID", senderID);
		m.put("datetime", sdf.format(new Date()));
		m.put("acquitted", acquitted);
		String xmlFragment = ConversionUtils.getXml(m, "message");
		System.out.println("#1#" + xmlFragment);
		boolean isPathced = xmlDataRepository.replacePatchValue(docID, xmlFragment, path);
		if (isPathced) {
			result.setStatus(HttpStatus.OK);
			result.setActionResultName(ActionResultName.SUCCESSFULL);
		} else {
			result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			result.setActionResultName(ActionResultName.FAIL);
			result.getMessages().add("PAtrching fragment fails on " + docID);
		}
		return result;
	}
}
