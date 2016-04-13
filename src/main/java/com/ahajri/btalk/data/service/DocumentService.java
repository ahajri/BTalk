package com.ahajri.btalk.data.service;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.WebAction;
import com.ahajri.btalk.data.domain.mapper.DataMapper;
import com.ahajri.btalk.data.repository.XmlDataRepository;
import com.ahajri.btalk.utils.ActionResultName;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.semantics.SPARQLQueryManager;

@Service("documentService")
public class DocumentService {

	@Autowired
	private XmlDataRepository xmlDataRepository;

	@Autowired
	private SPARQLQueryManager sparqlMgr;

	/**
	 * Persist document
	 * 
	 * @param jsonAction:
	 *            {@link WebAction}
	 * @return {@link Boolean} while action was successful or not
	 */
	public ActionResult createDocument(WebAction action, String collectionName) {
		ActionResult result = getSuccessResult();
		result.setJsonReturnData("{\"info\":\" :o Ooooops, Hello World\"}");
		// xmlFormOfBean = sos.toString();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		if (StringUtils.isNotBlank(collectionName)) {
			metadata.getCollections().add(collectionName);
		}
		// insert XML in Database
		if (StringUtils.isNotBlank(action.getDocument())) {
			// document already exists
			// TODO
		}
		// TODO: create document name
		String DIR = action.getDocument().substring(0, action.getDocument().lastIndexOf("/") + 1);
		//FIXME: convert to XML
		xmlDataRepository.persist(action.getJsonData(), metadata, DataMapper.mapDynaBean(action.getJsonData()), DIR);
		// get result

		return result;
	}

	private ActionResult getErrorResult() {
		ActionResult result = new ActionResult();
		result.setActionResultName(ActionResultName.FAIL);
		result.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		return result;
	}

	private ActionResult getSuccessResult() {
		ActionResult result = new ActionResult();
		result.setActionResultName(ActionResultName.SECCESSFULL);
		result.setStatus(HttpStatus.CREATED);
		return result;
	}
}
