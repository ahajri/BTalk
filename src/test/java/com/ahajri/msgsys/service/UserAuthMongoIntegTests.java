package com.ahajri.msgsys.service;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;

import com.ahajri.msgsys.config.MongoConfig;
import com.ahajri.msgsys.domain.UserAuth;
import com.google.gson.Gson;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAuthMongoIntegTests {

	private static final Logger LOGGER = Logger
			.getLogger(UserAuthMongoIntegTests.class);

	private static final String BASE_URL = "http://localhost:8282/msgsys/";

	private static HttpHeaders httpHeaders;
	private final static Gson gson = new Gson();
	private UserAuth created = null;

	@Autowired
	private RestTemplate restTemplate;

	@BeforeClass
	public static void setUp() {
		System.out.println("--------------Begin--------------");
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@Test
	public void aShouldCreate() throws Exception {
		LOGGER.info("<<<<<<<<<<<<<< Create >>>>>>>>>>>>> ");
		UserAuth model = new UserAuth();
		model.setEmail("ahajri@auxia.com");
		model.setPassword("1234");
		model.setRoles(Arrays.asList(new String[] { "rest-reader",
				"rest-writer" }));
		
		System.out.println(gson.toJson(model));
		HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(model),
				httpHeaders);
		ResponseEntity<UserAuth> responseEntity = restTemplate.exchange(
				BASE_URL + "createUser", HttpMethod.POST, entity, UserAuth.class);
		created = responseEntity.getBody();
		System.out.println("-------------  created  ---------------- "
				+ created);
		Assert.assertNotNull(responseEntity.getBody());
	}
}
