package com.ahajri.btalk.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;
import com.marklogic.client.ResourceNotFoundException;

@RestController
public class JsonDiscussController {

	private static final Logger logger = LoggerFactory
			.getLogger(JsonDiscussController.class);

	@Autowired
	protected DiscussionJsonRepository discussionJsonRepository;

	@RequestMapping(value = "/Discussions", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> createDiscussion(
			@RequestBody Discussion Discussion, UriComponentsBuilder builder) {
		discussionJsonRepository.add(Discussion);

		HttpHeaders headers = new HttpHeaders();
		// headers.setLocation(
		// builder.path("/discussion/{id}.json")
		// .buildAndExpand(Discussion.getSku()).toUri());

		return new ResponseEntity<>("", headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/discussion/{sku}.json", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteDiscussion(@PathVariable("sku") Long sku) {
		discussionJsonRepository.remove(null);
	}

	@RequestMapping(value = "/Discussions.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Discussion> searchDiscussions(
			@RequestParam(required = false, value = "name") String name) {
		if (StringUtils.isEmpty(name)) {
			logger.info("Lookup all {} Discussions...",
					discussionJsonRepository.count());
			return discussionJsonRepository.findAll();
		} else {
			logger.info("Lookup Discussions by name: {}", name);
			return discussionJsonRepository.findByQuery(name);
		}
	}

	// Example on how to register custom exception handler in case lookup does
	// not return
	// anything, and avoids HTTP status 500.
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public void handleMarkLogicResourceNotFoundException() {
	}

}
