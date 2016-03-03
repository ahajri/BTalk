package com.ahajri.btalk.controller;

import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;
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

	// @RequestMapping(value = "/discussion", method = RequestMethod.POST,
	// consumes = MediaType.APPLICATION_JSON_VALUE)
	// public ResponseEntity<String> create(@RequestBody Discussion Discussion,
	// UriComponentsBuilder builder) {
	// discussionJsonRepository.add(Discussion);
	//
	// HttpHeaders headers = new HttpHeaders();
	// headers.setLocation(
	// builder.path("/discussion/{id}.json")
	// .buildAndExpand(Discussion.getSku()).toUri());
	//
	// return new ResponseEntity<>("", headers, HttpStatus.CREATED);
	// }

	// @RequestMapping(value = "/discussion/{sku}.json", method =
	// RequestMethod.DELETE)
	// @ResponseStatus(HttpStatus.NO_CONTENT)
	// public void deleteDiscussion(@PathVariable("sku") Long sku) {
	// discussionJsonRepository.remove(null);
	// }

	// @RequestMapping(value = "/discussions.json", method = RequestMethod.GET,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// public List<Discussion> search(
	// @RequestParam(required = false, value = "name") String name) {
	// if (StringUtils.isEmpty(name)) {
	// logger.info("Lookup all {} Discussions...",
	// discussionJsonRepository.count());
	// return discussionJsonRepository.findAll();
	// } else {
	// logger.info("Lookup Discussions by name: {}", name);
	// return discussionJsonRepository.findByQuery(name);
	// }
	// }

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public void handleResourceNotFoundException() {
	}

	@Override
	public ResponseEntity<List<Discussion>> findByQuery(Discussion query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<Discussion> findOne(Discussion query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(Discussion query) {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/discuss/json/create", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<Discussion> create(@RequestBody Discussion model) {
		
		ResponseEntity<Discussion> resul = new ResponseEntity<Discussion>(
				model, HttpStatus.CREATED);
		discussionJsonRepository.add(model);

		return resul;
	}

	@Override
	public ResponseEntity<List<Discussion>> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
