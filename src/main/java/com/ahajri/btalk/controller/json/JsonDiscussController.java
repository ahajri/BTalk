package com.ahajri.btalk.controller.json;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.controller.AController;
import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.domain.upsert.DiscussionUpsert;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;
import com.marklogic.client.DatabaseClient;

@RestController
public class JsonDiscussController extends AController<Discussion> {

	private static final Logger LOGGER = Logger
			.getLogger(JsonDiscussController.class);

	@Autowired
	protected DiscussionJsonRepository discussionJsonRepository;

	@Autowired
	public DatabaseClient databaseClient;

	
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
		
		Discussion created = discussionJsonRepository.persist(model);
		return new ResponseEntity<Discussion>(created, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<List<Discussion>> findAll() {
		return null;
	}

	@RequestMapping(value = "/discuss/json/update", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<Discussion> update(
			@RequestBody DiscussionUpsert modelUpsert) {
		discussionJsonRepository.replaceInsert(modelUpsert.getModel(),
				modelUpsert.getFragment());
		return new ResponseEntity<Discussion>(modelUpsert.getModel(),
				HttpStatus.CREATED);
	}



}
