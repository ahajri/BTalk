package com.ahajri.btalk.controller;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
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
import com.ahajri.btalk.data.domain.json.SearchCriteria;
import com.ahajri.btalk.data.service.DocumentService;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.ahajri.btalk.utils.ActionResultName;
import com.ahajri.btalk.utils.ConversionUtils;
import com.ahajri.btalk.utils.SecurityUtils;
import com.marklogic.client.ResourceNotFoundException;

/**
 * Common Discussion Controller to manage messaging system
 * 
 * @author
 *         <p>
 *         ahajri
 *         <p>
 *
 */
@RestController
public class DiscussionController {

	private static final String XML_PREFIX = ".xml";
	/** discussion root node name */
	private static final String DISCUSS_ROOT_NODE = "discussion";
	/** message root node name */
	private static final String MESSAGE_ROOT_NODE = "message";
	/** Logger */
	private static final Logger LOGGER = Logger.getLogger(DiscussionController.class);
	/** Discussion MarkLogic collections */
	private static final List<String> DISCUSS_COLLECTIONS = Arrays.asList(new String[] { "DiscussionCollection" });
	/** Date Formatter */
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Autowired
	protected DocumentService documentService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/discuss/createDiscussion", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Object> create(@RequestBody Map action, @RequestHeader HttpHeaders headers,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position,
			@Context HttpServletRequest req) {
		LOGGER.debug("Create Document ....");
		// TODO: Get position later in LAT/LON
		Map<String, Object> metadata = new LinkedHashMap<String, Object>();
		metadata.put("remoteAddr", req.getRemoteAddr());
		metadata.put("remoteHost", req.getRemoteHost());
		metadata.put("remoteUser", req.getRemoteUser());
		metadata.put("remotePort", req.getRemotePort());
		//
		String docName = (String) action.get("document");
		if (docName == null) {
			docName = "/discuss/discussion_" + sdf.format(new Date()) + XML_PREFIX;
		}
		action.put("document", docName);
		String xml = getXmlData(action, headers, metadata, position);
		ActionResult result = documentService.createDocument(action, DISCUSS_COLLECTIONS, xml);
		return new ResponseEntity<Object>(result.getJsonReturnData(), result.getStatus());
	}

	/**
	 * Send Message Via JMS Broker and save it on database
	 * 
	 * @param action:
	 *            {@link Map} from JSON data
	 * @param position:
	 *            Latitude/Longitude position
	 * @return {@link ResponseEntity}
	 */
	@RequestMapping(value = "/discuss/sendMessage", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> sendMessage(@RequestBody Map<String, Object> action,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("Send Message ....");
		action.put("position", position);
		action.put("messageID", SecurityUtils.genUUID());
		String topicName = (String) action.get("topicName");
		String discussionID = (String) action.get("discussionID");
		String text = (String) action.get("textMessage");
		ActionResult jmsResult = null;

		if (!jmsResult.getActionResultName().equals(ActionResultName.FAIL)) {
			// TODO: Save Message in database

		}
		// ActionResult result = documentService.createDocument(action,
		// DISCUSS_COLLECTIONS, xml);
		return new ResponseEntity<Object>(jmsResult.getJsonReturnData(), jmsResult.getStatus());
	}

	@RequestMapping(value = "/discuss/readMessage", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> readMessage(@RequestBody Map<String, Object> action,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("Read message ....");
		action.put("position", position);
		action.put("messageID", SecurityUtils.genUUID());
		String topicName = (String) action.get("topicName");
		String discussionID = (String) action.get("discussionID");
		ActionResult jmsResult = null;

		if (jmsResult != null && !jmsResult.getActionResultName().equals(ActionResultName.FAIL)) {
			// TODO: change message status

		}
		// ActionResult result = documentService.createDocument(action,
		// DISCUSS_COLLECTIONS, xml);
		return new ResponseEntity<Object>(jmsResult.getJsonReturnData(), jmsResult.getStatus());
	}

	/**
	 * 
	 * @param action
	 * @param position
	 * @return
	 */
	@RequestMapping(value = "/discuss/endDiscussion", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> endDiscussion(@RequestBody Map action,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("End discussion ...." + action.toString());
		ActionResult result = documentService.endDiscussion(action);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/discuss/addMessage", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> patchMessage(@RequestBody Map map,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		map.put("position", position);
		LOGGER.debug("Patch Document ...." + map.toString());
		ActionResult result = documentService.patchFragment(map);
		return new ResponseEntity<Object>(result.getJsonReturnData(), result.getStatus());
	}

	@RequestMapping(value = "/discuss/deleteTag", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> deleteFragment(@RequestBody Map map,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		map.put("position", position);
		LOGGER.debug("Patch Document ...." + map.toString());
		ActionResult result = documentService.deleteTag(map);
		return new ResponseEntity<Object>(result.getJsonReturnData(), result.getStatus());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/discuss/deleteDiscussion", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Object> deleteMessage(@RequestBody Map map,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("delete Document ...." + map.toString());
		List<String> urilList = (List<String>) map.get("docURIs");
		String[] docURIs = new String[urilList.size()];
		for (int i = 0; i < urilList.size(); i++) {
			docURIs[i] = urilList.get(0);
		}
		ActionResult result = documentService.deleteDocument(docURIs);
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
	private String getXmlData(Map action, HttpHeaders headers, Map<String, Object> metadata, String position) {
		Map map = new HashMap<>();
		map.put("headers", headers.toSingleValueMap());
		map.put("metadata", (Map<String, Object>) metadata);
		map.put("position", position);
		map.put("document", (String) action.get("document"));
		map.put("messages", null);// to add messages under this element later
		map.put("startTime", new Date());
		map.put("endTime", null);
		map.put("discussID", SecurityUtils.genUUID());
		map.putAll((LinkedHashMap) action.get("jsonData"));
		return ConversionUtils.getXml(map, DISCUSS_ROOT_NODE);
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
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource Not Found, Please ensure the document already exists")
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
	// @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	// @ExceptionHandler(Exception.class)
	// public ResponseEntity<ClientErrorInformation>
	// handleException(HttpServletRequest req, Exception ex) {
	// ex.printStackTrace();
	// ClientErrorInformation e = new ClientErrorInformation(ex.getMessage(),
	// HttpStatus.INTERNAL_SERVER_ERROR.toString());
	// return new ResponseEntity<ClientErrorInformation>(e,
	// HttpStatus.INTERNAL_SERVER_ERROR);
	// }
}
