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
import com.ahajri.btalk.data.service.DiscussionService;

@RestController
public class JsonDiscussController extends AController<Discussion> {

	private static final Logger LOGGER = Logger
			.getLogger(JsonDiscussController.class);

	@Autowired
	protected DiscussionService discussionService;

	@RequestMapping(value = "/discuss/json/search", method = RequestMethod.GET, params = { "q" })
	@ResponseStatus(HttpStatus.FOUND)
	@Override
	public ResponseEntity<List<Discussion>> findByQuery(
			@RequestParam("q") String q) {
		LOGGER.info("Search Discussion by Query: " + q);
		List<Discussion> discussions = discussionService.findByQuery(q);

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
		Discussion created = discussionService.create(model);
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
		discussionService.replaceInsert(modelUpsert.getModel(),
				modelUpsert.getFragment());
		return new ResponseEntity<Discussion>(modelUpsert.getModel(),
				HttpStatus.CREATED);
	}

	@RequestMapping(value = "/discuss/json/search", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.FOUND)
	@Override
	public ResponseEntity<List<Discussion>> search(String q) {
		return new ResponseEntity<List<Discussion>>(
				discussionService.search(q), HttpStatus.FOUND);
	}

	@RequestMapping(value = "/discuss/json/addMessage", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<Discussion> addMessage(
			@RequestBody DiscussionUpsert model) {
		try {
			return new ResponseEntity<Discussion>(
					discussionService.addMessage(model), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(e);
			return new ResponseEntity<Discussion>(model.getModel(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
