package com.ahajri.btalk.controller.json;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.controller.AController;
import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.domain.upsert.DiscussUpsert;
import com.ahajri.btalk.data.domain.upsert.DiscussionUpsert;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.ResourceNotFoundException;

@RestController
public class JsonDiscussController extends AController<Discussion> {

	private static final Logger LOGGER = Logger
			.getLogger(JsonDiscussController.class);

	@Autowired
	protected DiscussionJsonRepository discussionJsonRepository;

	@Autowired
	public DatabaseClient databaseClient;

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public void handleResourceNotFoundException() {
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientErrorInformation> handleException(HttpServletRequest req, Exception ex) {
		ex.printStackTrace();
		ClientErrorInformation e=new ClientErrorInformation(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return new ResponseEntity<ClientErrorInformation>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@RequestMapping(value = "/discuss/json/search", method = RequestMethod.GET, params = { "q" })
	@ResponseStatus(HttpStatus.FOUND)
	@Override
	public ResponseEntity<List<Discussion>> findByQuery(
			@RequestParam("q") String q) {
		
		List<Discussion> discussions = discussionJsonRepository.findByQuery(q);
		ResponseEntity<List<Discussion>> result = new ResponseEntity<List<Discussion>>(
				discussions, HttpStatus.FOUND);
		return result;
	}

	@Override
	public ResponseEntity<Discussion> findOne(Discussion query) {
		return null;
	}

	@Override
	public Integer delete(Discussion query) {
		return null;
	}

	@RequestMapping(value = "/discuss/json/create", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<Discussion> create(@RequestBody Discussion model) {
		
		Discussion created = discussionJsonRepository.add(model);
		return new ResponseEntity<Discussion>(created, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<List<Discussion>> findAll() {
		return null;
	}

	@RequestMapping(value = "/discuss/json/update", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<Discussion> update(
			@RequestBody DiscussUpsert modelUpsert) {
		discussionJsonRepository.replaceInsert(modelUpsert.getModel(),
				modelUpsert.getFragment());
		return new ResponseEntity<Discussion>(modelUpsert.getModel(),
				HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<Discussion> update(DiscussionUpsert modelUpsert) {
		// TODO Auto-generated method stub
		return null;
	}

}
