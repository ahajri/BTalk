package com.ahajri.msgsys.service;

import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ahajri.msgsys.config.AppConfig;
import com.ahajri.msgsys.domain.IModel;
import com.ahajri.msgsys.domain.UserAuth;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Integration test for http web service client
 * 
 * @author <p>
 *         ahajri
 *         </p>
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AppConfig.class })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HttpClientMongoUserIntegTests {

	private static final Logger LOGGER = Logger
			.getLogger(HttpClientMongoUserIntegTests.class);

	private static final String HOST = "localhost";
	private static final String PROTOCOL = "http";
	private static final String CHARSET = "UTF-8";

	private static final Gson gson = new Gson();

	private static IModel created;

	@BeforeClass
	public static void setUp() {
		System.out.println("--------------set up------------------");

	}

	@Test
	public void atestHttpClientCreate() throws Exception {

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		try {
			// specify the host, protocol, and port
			HttpHost target = new HttpHost(HOST, 8282, PROTOCOL);

			// specify the get request
			HttpPost postRequest = new HttpPost("/msgsys/createUser");

			UserAuth model = new UserAuth();
			model.setEmail("ahajri@auxia.com");
			model.setPassword("1234");
			model.setRoles(Arrays.asList(new String[]{"rest-reader","rest-writer"}));

			StringEntity input = new StringEntity(gson.toJson(model));
			input.setContentType("application/json");
			postRequest.setEntity(input);

			LOGGER.info(">>>>>>>>>> executing request to " + target);

			HttpResponse httpResponse = httpclient.execute(target, postRequest);
			HttpEntity entity = httpResponse.getEntity();

			System.out.println("----------------------------------------");
			LOGGER.info(">>>>>>>>>>" + httpResponse.getStatusLine());
			Header[] headers = httpResponse.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			if (entity != null) {
				String json = EntityUtils.toString(entity, CHARSET);
				JsonElement jelem = gson.fromJson(json, JsonElement.class);
				JsonObject jobj = jelem.getAsJsonObject();
				created = gson.fromJson(jobj, UserAuth.class);
				System.out.println("created : " + created);

			} else {
				Assert.fail();
			}
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		Assert.assertTrue(true);

	}

	@Ignore
	@Test
	public void btestHttpClientFindAll() throws Exception {

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		try {
			// specify the host, protocol, and port
			HttpHost target = new HttpHost(HOST, 2525, PROTOCOL);

			// specify the get request
			HttpGet getRequest = new HttpGet("/msgsys/findUser");

			LOGGER.info(">>>>>>>>>>executing request to " + target);

			HttpResponse httpResponse = httpclient.execute(target, getRequest);
			HttpEntity entity = httpResponse.getEntity();

			System.out.println("----------------------------------------");
			LOGGER.info(">>>>>>>>>>" + httpResponse.getStatusLine());
			Header[] headers = httpResponse.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			if (entity != null) {
				System.out.println(EntityUtils.toString(entity, CHARSET));
			}
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		Assert.assertTrue(true);
	}

	@Ignore
	@Test
	public void ctestHttpClientDelete() throws Exception {

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();

		try {
			// specify the host, protocol, and port
			HttpHost target = new HttpHost(HOST, 2525, PROTOCOL);

			// specify the get request
			HttpPost postRequest = new HttpPost("/msgsys/deleteUser");

			StringEntity input = new StringEntity(gson.toJson(created));
			input.setContentType("application/json");
			postRequest.setEntity(input);

			LOGGER.info(">>>>>>>>>> executing request to " + target);

			HttpResponse httpResponse = httpclient.execute(target, postRequest);
			HttpEntity entity = httpResponse.getEntity();

			System.out.println("----------------------------------------");
			LOGGER.info(">>>>>>>>>>" + httpResponse.getStatusLine());
			Header[] headers = httpResponse.getAllHeaders();
			for (int i = 0; i < headers.length; i++) {
				System.out.println(headers[i]);
			}
			if (entity != null) {
				int count = Integer.parseInt(EntityUtils.toString(entity,
						CHARSET));
				System.out.println("deleted elements Count = " + count);
				Assert.assertSame(1, count);
			} else {
				Assert.fail();
			}
			System.out.println("----------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		} finally {
			httpclient.getConnectionManager().shutdown();
		}
		Assert.assertTrue(true);

	}

	@AfterClass
	public static void tearDown() {
		System.out.println("--------------tear Down--------------");
	}

}
