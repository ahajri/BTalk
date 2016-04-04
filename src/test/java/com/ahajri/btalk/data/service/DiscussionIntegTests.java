package com.ahajri.btalk.data.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

import com.ahajri.btalk.config.MarkLogicConfig;
import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.domain.DiscussionMember;
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
	private DiscussionMember created = null;

	@Autowired
	private RestTemplate restTemplate;

	@BeforeClass
	public static void setUp() {
		httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
	}

	@Test
	public void aShouldCreate() throws Exception {

		LOGGER.info("<<<<<<<<<<<<<< Create >>>>>>>>>>>>> ");

		DiscussionMember member1 = new DiscussionMember();
		member1.setMember_id("ahajri@auxia.com");
		member1.setStatus(DiscussStatus.ONLINE.getValue());
		member1.setDiscussRole(DiscussRole.DISCUSS_CREATOR.getValue());

		DiscussionMember member2 = new DiscussionMember();
		member2.setMember_id("aoulagha@auxia.com");
		member2.setStatus(DiscussStatus.ONLINE.getValue());
		member2.setDiscussRole(DiscussRole.DISCUSS_MEMBER.getValue());
		List<DiscussionMember> members = new ArrayList<DiscussionMember>();
		members.addAll(Arrays.asList(member1, member2));

		Discussion discuss = new Discussion();
		discuss.setStartTime(new Date(System.currentTimeMillis()));
		discuss.setMembers(members);

		System.out.println(gson.toJsonTree(discuss));
		HttpEntity<Discussion> entity = new HttpEntity<Discussion>(discuss,
				httpHeaders);
		ResponseEntity<DiscussionMember> responseEntity = restTemplate
				.exchange(BASE_URL + "/discuss/json/create", HttpMethod.POST,
						entity, DiscussionMember.class);
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
