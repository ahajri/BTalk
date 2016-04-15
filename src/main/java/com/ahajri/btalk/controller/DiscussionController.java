package com.ahajri.btalk.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
import com.ahajri.btalk.data.domain.json.WebAction;
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

	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DiscussionController.class);
	/** Discussion MarkLogic collections */
	List<String> discussionCollections = Arrays.asList(new String[] { "DiscussionCollection" });
	/**Date Formatter*/
	private static final SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmss");
	@Autowired
	protected DocumentService documentService;

	@RequestMapping(value = "/discuss/createDocument", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> create(@RequestBody WebAction action, @RequestHeader HttpHeaders headers,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("Create Document ....");
		// check doc exist or not

		// TODO: Get position later in LAT/LON
		
		String docName = action.getDocument();
		if (docName == null) {

			//get max ID
			docName="/discuss/discussion_"+sdf.format(new Date())+".xml";
			action.setDocument(docName);
		}
		String xml = getXmlData(action, headers, position);
		ActionResult result = documentService.createDocument(action, discussionCollections, xml);
		return new ResponseEntity<String>(result.getJsonReturnData(), result.getStatus());

	}
	
	@RequestMapping(value = "/discuss/searchDocument", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.FOUND)
	public ResponseEntity<String> search(@RequestBody SearchCriteria criteria, @RequestHeader HttpHeaders headers,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("Search Document ....");
		// check doc exist or not

		// TODO: Get position later in LAT/LON
		
	
		ActionResult result = documentService.searchDocument(criteria, discussionCollections);
		return new ResponseEntity<String>(result.getJsonReturnData(), result.getStatus());

	}

	private String getXmlData(WebAction action, HttpHeaders headers, String position) {
		XmlMap xmlMap = new XmlMap();
		xmlMap.putAll(headers.toSingleValueMap());
		xmlMap.put("position", position);
		xmlMap.put("document", action.getDocument());
		xmlMap.putAll((LinkedHashMap) action.getJsonData());
		Iterator<Entry<String, Object>> iterator = xmlMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			Object value = entry.getValue();
			Class valueClass = null;
			if (value != null) {
				valueClass = value.getClass();
			}
		}
		XStream magicApi = new XStream();
		magicApi.registerConverter(new MapEntryConverter());
		magicApi.alias("discussion", XmlMap.class);

		String xml = magicApi.toXML(xmlMap);
		return xml;
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
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
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
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientErrorInformation> handleException(HttpServletRequest req, Exception ex) {
		ex.printStackTrace();
		ClientErrorInformation e = new ClientErrorInformation(ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return new ResponseEntity<ClientErrorInformation>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
