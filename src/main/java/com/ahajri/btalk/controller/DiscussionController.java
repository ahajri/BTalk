package com.ahajri.btalk.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.converter.MapEntryConverter;
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.ahajri.btalk.data.service.DocumentService;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.marklogic.client.ResourceNotFoundException;
import com.thoughtworks.xstream.XStream;

/**
 * Common Discussion Controller to manage given JSON data from the web service
 * 
 * @author ahajri
 *
 */
@RestController
public class DiscussionController {

	private static final String DISCUSS_ROOT_NODE = "discussion";
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DiscussionController.class);
	/** Discussion MarkLogic collections */
	private static final List<String> DISCUSS_COLLECTIONS = Arrays.asList(new String[] { "DiscussionCollection" });
	/** Date Formatter */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	@Autowired
	protected DocumentService documentService;

	@RequestMapping(value = "/discuss/createDocument", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestBody XmlMap action, @RequestHeader HttpHeaders headers,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position,
			@Context HttpServletRequest req) {
		LOGGER.debug("Create Document ....");
		// TODO: Get position later in LAT/LON
		HashMap<String, Object> metadata = new HashMap<String, Object>();
		metadata.put("remoteAddr", req.getRemoteAddr());
		metadata.put("remoteHost", req.getRemoteHost());
		metadata.put("remoteUser", req.getRemoteUser());
		metadata.put("remotePort", req.getRemotePort());
		String docName = (String) action.get("document");
		if (docName == null) {
			docName = "/discuss/discussion_" + sdf.format(new Date()) + ".xml";
			action.put("document", docName);
		}
		String xml = getXmlData(action, headers, metadata, position);
		ActionResult result = documentService.createDocument(action, DISCUSS_COLLECTIONS, xml);
		return new ResponseEntity<Object>(result.getJsonReturnData(), result.getStatus());

	}

	@RequestMapping(value = "/discuss/searchByKeyValue", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.FOUND)
	public ResponseEntity<Object> searchByKeyValue(@RequestBody SearchCriteria criteria,
			@RequestHeader HttpHeaders headers,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("Search Document ...." + criteria.toString());
		ActionResult result = documentService.searchByKeyValue(criteria, DISCUSS_COLLECTIONS);
		return new ResponseEntity<Object>(result.getJsonReturnData(), result.getStatus());
	}

	/**
	 * 
	 * @param action
	 * @param headers
	 * @param metadata
	 * @param position
	 * @return
	 */

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String getXmlData(XmlMap action, HttpHeaders headers, HashMap<String, Object> metadata, String position) {
		XmlMap xmlMap = new XmlMap();
		xmlMap.put("headers", headers.toSingleValueMap());
		xmlMap.put("metadata", (Map<String, Object>) metadata);
		xmlMap.put("position", position);
		xmlMap.put("document", (String) action.get("document"));
		xmlMap.put("messages", null);// to add messages under this element later
		xmlMap.put("startTime", new Date());
		xmlMap.put("endTime", null);
		xmlMap.putAll((LinkedHashMap) action.get("jsonData"));
		return getXml(xmlMap, DISCUSS_ROOT_NODE);
	}

	/**
	 * 
	 * @param xmlMap
	 * @param rootName
	 * @return
	 */
	private String getXml(XmlMap xmlMap, String rootName) {
		XStream magicApi = new XStream();
		magicApi.registerConverter(new MapEntryConverter());
		magicApi.alias(rootName, XmlMap.class);
		return magicApi.toXML(xmlMap);
	}

	/**
	 * Handle {@link ClientErrorInformation}
	 * 
	 * @param req:
	 *            {@link HttpServletRequest}
	 * @param ex
	 *            {@link ResourceNotFoundException}
	 * @return {@link ClientErrorInformation}
	 */
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource Not Found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ClientErrorInformation> handleResourceNotFoundException(HttpServletRequest req,
			ResourceNotFoundException ex) {
		ClientErrorInformation e = new ClientErrorInformation(ex.getMessage(), HttpStatus.NOT_FOUND.toString());
		return new ResponseEntity<ClientErrorInformation>(e, HttpStatus.NOT_FOUND);
	}

	/**
	 * Handle {@link Exception}
	 * 
	 * @param req:
	 *            {@link HttpServletRequest}
	 * @param ex:
	 *            {@link Exception}
	 * @return {@link ClientErrorInformation}
	 */
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Oooops, Internal Server Error")
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientErrorInformation> handleException(HttpServletRequest req, Exception ex) {
		ex.printStackTrace();
		ClientErrorInformation e = new ClientErrorInformation(ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return new ResponseEntity<ClientErrorInformation>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
