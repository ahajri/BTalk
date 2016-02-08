package com.ahajri.msgsys.controller;

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

import com.ahajri.msgsys.domain.UserAuth;
import com.ahajri.msgsys.service.UserAuthService;

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
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(value = "/msgsys/createUser", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@Override
	public ResponseEntity<UserAuth> create(@RequestBody UserAuth model) {
		ResponseEntity<UserAuth> response = new ResponseEntity<UserAuth>(model,
				HttpStatus.CREATED);
		try {
			userService.persist(model);
		} catch (Exception e) {
			response = new ResponseEntity<UserAuth>(model, HttpStatus.NOT_FOUND);
		}
		return response;
	}

}
