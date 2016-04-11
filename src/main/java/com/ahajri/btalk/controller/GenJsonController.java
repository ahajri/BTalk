package com.ahajri.btalk.controller;

import java.util.Enumeration;

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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.JsonAction;
import com.ahajri.btalk.data.service.GenericJsonService;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.marklogic.client.ResourceNotFoundException;

/**
 * Generic Controller to manage given JSON data from the web service
 * 
 * @author ahajri
 *
 */
@RestController
public class GenJsonController {

	private static final Logger LOGGER = Logger.getLogger(GenJsonController.class);

	@Autowired
	protected GenericJsonService generciJsonService;

	@RequestMapping(value = "/data/createDocument", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<String> create(@RequestBody JsonAction action, @RequestHeader HttpHeaders headers,
			@CookieValue("position") String position) {
		LOGGER.debug("Create Document....");
		//TODO: Get position later in LAT/LON
		action.getMetadata().putAll(headers.toSingleValueMap());
		ActionResult result = generciJsonService.createDocument(action);
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
