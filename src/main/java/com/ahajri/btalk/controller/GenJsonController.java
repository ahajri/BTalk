package com.ahajri.btalk.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.data.domain.ActionResult;
import com.ahajri.btalk.data.domain.json.JsonAction;
import com.ahajri.btalk.data.service.GenericJsonService;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.ahajri.btalk.utils.ActionResultName;
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
	public ResponseEntity<String> create(@RequestBody JsonAction model) {
		LOGGER.info("...Create Document...");
		ActionResult result = generciJsonService.createDocument(model);
		if (result.getActionResultName().equals(ActionResultName.FAIL.getName())) {
			return new ResponseEntity<String>(result.getJsonReturnData(), HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<String>(result.getJsonReturnData(), HttpStatus.CREATED);
		}

	}

	/*
	 * Error Handlers
	 */
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public void handleResourceNotFoundException() {
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientErrorInformation> handleException(HttpServletRequest req, Exception ex) {
		ex.printStackTrace();
		ClientErrorInformation e = new ClientErrorInformation(ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return new ResponseEntity<ClientErrorInformation>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
