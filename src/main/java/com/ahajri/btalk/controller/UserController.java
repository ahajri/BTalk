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
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.data.domain.UserAuth;
import com.ahajri.btalk.data.service.UserAuthService;

@RestController
public class UserController extends AMongoController<UserAuth> {

	/** LOGGER */
	private static final Logger LOGGER = Logger.getLogger(UserController.class);

	@Autowired
	private UserAuthService userService = new UserAuthService();

	@Override
	public ResponseEntity<List<UserAuth>> findByQuery(UserAuth query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResponseEntity<UserAuth> findOne(UserAuth query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer delete(UserAuth query) {
		return null;
	}

	@RequestMapping(value = "/btalk/createUser", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<UserAuth> create(@RequestBody UserAuth model) {
		ResponseEntity<UserAuth> response = new ResponseEntity<UserAuth>(model,
				HttpStatus.CREATED);
		try {
			if (userService.persist(model) == null) {
				response = new ResponseEntity<UserAuth>(model,
						HttpStatus.EXPECTATION_FAILED);
			}
		} catch (Exception e) {
			response = new ResponseEntity<UserAuth>(model, HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@RequestMapping(value = "/btalk/allUsers", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.FOUND)
	@Override
	public ResponseEntity<List<UserAuth>> findAll() {
		return new ResponseEntity<List<UserAuth>>(userService.findAll(),
				HttpStatus.FOUND);
	}

}
