package com.ahajri.btalk.data.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.data.domain.json.WebAction;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.ahajri.btalk.data.repository.XmlDataRepository;
import com.ahajri.btalk.utils.ActionResultName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.jayway.jsonpath.Criteria;
import com.marklogic.client.io.DocumentMetadataHandle;

@Service("documentService")
public class DocumentService {

	@Autowired
	private XmlDataRepository xmlDataRepository;

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
	public ActionResult createDocument(XmlMap action, List<String> collectionNames, String xml) {
		ActionResult result = getSuccessResult();
		result.setJsonReturnData("{\"info\":\" O yeeeeah, Well done man\"}");
		// xmlFormOfBean = sos.toString();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		if (!ListUtils.isEqualList(collectionNames, null)) {
			metadata.getCollections().addAll(collectionNames);
		}

		// FIXME: convert to XML
		try {
			xmlDataRepository.persist(xml, metadata, (String) action.get("document"));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			result = getErrorResult();
			result.setJsonReturnData("{\"msg\":\" " + e.getMessage() + "\"}");
		} catch (IOException e) {
			e.printStackTrace();
			result = getErrorResult();
			result.setJsonReturnData("{\"msg\":\" " + e.getMessage() + "\"}");
		}
		// get result

		return result;
	}

	public ActionResult searchDocument(SearchCriteria criteria, List<String> collectionNames) {
		ActionResult result = new ActionResult();
		result.setActionResultName(ActionResultName.SUCCESSFULL);
		result.setStatus(HttpStatus.FOUND);

		try {
			List<XmlMap> found = xmlDataRepository.searchDocument(criteria.getQuery());
			StringBuffer buffer = new StringBuffer("{\"found\":[");
			for (XmlMap xmlMap : found) {
				buffer.append(new Gson().toJson(xmlMap));
			}
			buffer.append("]}");
			result.setJsonReturnData(buffer.toString());
		} catch (IOException e) {
			result = getErrorResult();
			result.setJsonReturnData("{msg:+" + e.getMessage() + "}");
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
	private ActionResult getSuccessResult() {
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
		List<XmlMap> foundData = null;
		try {
			foundData = xmlDataRepository.searchByKeyValue(criteria, discussCollections, metadata);
		} catch (IOException e) {
			result.setActionResultName(ActionResultName.FAIL);
			result.setStatus(HttpStatus.NOT_FOUND);
			result.setJsonReturnData("{\"msg\":\"" + e.getMessage() + "\"}");
		}
		result.setStatus(HttpStatus.FOUND);
		result.setActionResultName(ActionResultName.SUCCESSFULL);
		result.setJsonReturnData(foundData);
		return result;
	}
}
