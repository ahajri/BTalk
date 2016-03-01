package com.ahajri.btalk.data.service;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.ahajri.btalk.config.MongoConfig;
import com.ahajri.btalk.data.domain.UserAuth;
import com.google.gson.Gson;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MongoConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAuthMongoIntegTests {

	private static final Logger LOGGER = Logger
			.getLogger(UserAuthMongoIntegTests.class);

	private static final String BASE_URL = "http://localhost:8282/btalk/";

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

	@Ignore
	@Test
	public void aShouldCreate() throws Exception {
		LOGGER.info("<<<<<<<<<<<<<< Create >>>>>>>>>>>>> ");
		UserAuth model = new UserAuth();
		model.setEmail("anis.hajri@gmail.com");
		model.setPassword("mafhh14$");
		model.setRoles(Arrays.asList(new String[] { "super-admin" }));

		System.out.println(gson.toJson(model));
		HttpEntity<String> entity = new HttpEntity<String>(gson.toJson(model),
				httpHeaders);
		ResponseEntity<UserAuth> responseEntity = restTemplate.exchange(
				BASE_URL + "createUser", HttpMethod.POST, entity,
				UserAuth.class);
		created = responseEntity.getBody();
		System.out.println("-------------  created  ---------------- "
				+ created);
		Assert.assertNotNull(responseEntity.getBody());
	}

	@Test
	public void aShouldFind() throws Exception {
		LOGGER.info("<<<<<<<<<<<<<< Find All >>>>>>>>>>>>> ");

		UriComponentsBuilder builder = UriComponentsBuilder
				.fromHttpUrl(BASE_URL + "allUsers");
		HttpEntity<?> entity = new HttpEntity<>(httpHeaders);
		HttpEntity<String> response = restTemplate.exchange(builder.build()
				.encode().toUri(), HttpMethod.GET, entity, String.class);

		System.out.println("-------------  Found  ---------------- "
				+ response.getBody());
		Assert.assertNotNull(response.getBody());
	}
}
