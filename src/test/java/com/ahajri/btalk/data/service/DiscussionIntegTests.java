package com.ahajri.btalk.data.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.ahajri.btalk.config.MarkLogicConfig;
import com.ahajri.btalk.utils.DiscussRole;
import com.ahajri.btalk.utils.DiscussStatus;
import com.google.gson.Gson;

@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { MarkLogicConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DiscussionIntegTests {

	private static final Logger LOGGER = Logger
			.getLogger(DiscussionIntegTests.class);

	private static final String BASE_URL = "http://localhost:9000/";
	protected static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddhhmmss");

	private static HttpHeaders httpHeaders;
	private final static Gson gson = new Gson();
	private Map<String, Object> created = null;

	@Autowired
	private RestTemplate restTemplate;

	@BeforeClass
	public static void setUp() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void aShouldCreate() throws Exception {

		LOGGER.info("<<<<<<<<<<<<<< Create >>>>>>>>>>>>> ");

		HashMap member1 = new HashMap();
		member1.put("member_id","ahajri@auxia.com");
		member1.put("status",DiscussStatus.ONLINE.getValue());
		member1.put("discuss_role",DiscussRole.DISCUSS_CREATOR.getValue());

		HashMap member2 = member1;
		List<HashMap> members = new ArrayList<HashMap>();
		members.addAll(Arrays.asList(member1, member2));

		HashMap discuss = new HashMap();
		discuss.put("start_time",new Date(System.currentTimeMillis()));
		discuss.put("members",members);

		System.out.println(gson.toJsonTree(discuss));
		HttpEntity<Map> entity = new HttpEntity<Map>(discuss,
				httpHeaders);
		ResponseEntity<HashMap> responseEntity = restTemplate
				.exchange(BASE_URL + "/discuss/json/create", HttpMethod.POST,
						entity, HashMap.class);
		created = responseEntity.getBody();
		System.out.println("-------------  created  ---------------- "
				+ created);
		Assert.assertNotNull(responseEntity.getBody());
	}

	@Ignore
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
