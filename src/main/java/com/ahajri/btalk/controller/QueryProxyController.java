package com.ahajri.btalk.controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ahajri.btalk.controller.json.JsonDiscussController;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RestController
public class QueryProxyController {

	private static final Logger LOGGER = Logger
			.getLogger(JsonDiscussController.class);

	@Autowired
	public Client jerseyClient;

	@Autowired
	public String markLogicBaseURL;

	@RequestMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> query(HttpServletRequest req) {
		
		WebResource webResource = jerseyClient.resource(String.format(
				"%s/v1/search", markLogicBaseURL));
		webResource.accept(javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE,
				javax.ws.rs.core.MediaType.APPLICATION_ATOM_XML_TYPE,
				javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE);
		webResource.setProperty("Content-type", "application/json");
		String payload = "";
		MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
		queryParams.add("format", "json");
		queryParams.add("view", "all");
		queryParams.add("options", "all");
		queryParams.add("start", "1");
		queryParams.add("pageLength", "10");
		ClientResponse response = webResource.queryParams(queryParams)
				.type("application/json").post(ClientResponse.class, payload);
		return new ResponseEntity<String>(response.getEntity(String.class),
				HttpStatus.valueOf(response.getStatus()));
	}

}
