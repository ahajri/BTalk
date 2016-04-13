package com.ahajri.btalk.controller;

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
import com.ahajri.btalk.data.domain.json.WebAction;
import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.ahajri.btalk.data.service.DocumentService;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.marklogic.client.ResourceNotFoundException;

/**
 * Generic Controller to manage given JSON data from the web service
 * 
 * @author ahajri
 *
 */
@RestController
public class MainController {

	private static final Logger LOGGER = Logger.getLogger(MainController.class);

	@Autowired
	protected DocumentService generciJsonService;

	@RequestMapping(value = "/data/createDocument", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> create(@RequestBody WebAction action, @RequestHeader HttpHeaders headers,
			@CookieValue(value = "position", defaultValue = "48.890019, 2.316873") String position) {
		LOGGER.debug("Create Document ....");
		// TODO: Get position later in LAT/LON
		action.getMetadata().putAll(headers.toSingleValueMap());
		action.getMetadata().put("position", position);
		XmlMap xmlMap = new XmlMap();
		xmlMap.putAll(action.getMetadata());
		xmlMap.put("document_id", action.getDocument());
		System.out.println("#####Controller#####" + xmlMap);
		ActionResult result = generciJsonService.createDocument(action, "DiscussionCollection");
		return new ResponseEntity<String>(result.getJsonReturnData(), result.getStatus());

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
