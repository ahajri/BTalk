package com.ahajri.btalk.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ahajri.btalk.data.domain.DiscussionMember;
import com.ahajri.btalk.data.service.UserAuthService;

//@RestController
public class UserController extends AController<DiscussionMember> {

	/** LOGGER */
	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	@Autowired
	private UserAuthService userService = new UserAuthService();

	@Override
	public ResponseEntity<List<DiscussionMember>> findByQuery(String query) {
		
		return null;
	}

	@Override
	public ResponseEntity<DiscussionMember> findOne(DiscussionMember query) {
		
		return null;
	}

	@Override
	public ResponseEntity<String> delete(DiscussionMember query) {
		return null;
	}

	@RequestMapping(value = "/btalk/createUser", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<DiscussionMember> create(@RequestBody DiscussionMember model) {
		ResponseEntity<DiscussionMember> response = new ResponseEntity<DiscussionMember>(model,
				HttpStatus.CREATED);
		try {
			if (userService.create(model) == null) {
				response = new ResponseEntity<DiscussionMember>(model,
						HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			response = new ResponseEntity<DiscussionMember>(model, HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@RequestMapping(value = "/btalk/allUsers", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.FOUND)
	@Override
	public ResponseEntity<List<DiscussionMember>> findAll() {
		return new ResponseEntity<List<DiscussionMember>>(userService.findAll(),
				HttpStatus.FOUND);
	}

	@Override
	public ResponseEntity<List<DiscussionMember>> search(String q) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
