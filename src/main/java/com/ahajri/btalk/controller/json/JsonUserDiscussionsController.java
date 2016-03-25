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
import com.ahajri.btalk.data.domain.UserDiscussions;
import com.ahajri.btalk.data.domain.upsert.DiscussionsUpsert;
import com.ahajri.btalk.data.repository.UserDiscussionsJsonRepository;
import com.marklogic.client.DatabaseClient;

@RestController
public class JsonUserDiscussionsController extends AController<UserDiscussions> {

	private static final Logger LOGGER = Logger
			.getLogger(JsonUserDiscussionsController.class);

	@Autowired
	protected UserDiscussionsJsonRepository userDiscussionsJsonRepository;

	@Autowired
	public DatabaseClient databaseClient;

	

	@RequestMapping(value = "/discussions/json/search", method = RequestMethod.GET, params = { "q" })
	@ResponseStatus(HttpStatus.FOUND)
	@Override
	public ResponseEntity<List<UserDiscussions>> findByQuery(
			@RequestParam("q") String q) {
		List<UserDiscussions> discussions = userDiscussionsJsonRepository.findByQuery(q);
		ResponseEntity<List<UserDiscussions>> result = new ResponseEntity<List<UserDiscussions>>(
				discussions, HttpStatus.FOUND);
		return result;
	}

	@Override
	public ResponseEntity<UserDiscussions> findOne(UserDiscussions query) {
		return null;
	}

	@Override
	public Integer delete(UserDiscussions query) {
		return null;
	}

	@RequestMapping(value = "/discussions/json/create", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<UserDiscussions> create(@RequestBody UserDiscussions model) {
		UserDiscussions created = userDiscussionsJsonRepository.persist(model);
		return new ResponseEntity<UserDiscussions>(created, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<List<UserDiscussions>> findAll() {
		return null;
	}

	@RequestMapping(value = "/discussions/json/update", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<UserDiscussions> update(
			@RequestBody DiscussionsUpsert modelUpsert) {
		userDiscussionsJsonRepository.replaceInsert(modelUpsert.getModel(),
				modelUpsert.getFragment());
		return new ResponseEntity<UserDiscussions>(modelUpsert.getModel(),
				HttpStatus.CREATED);
	}
	

}
