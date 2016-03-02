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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.repository.DiscussionXmlRepository;

@RestController
public class XmlTalkController {

	private static final Logger logger = LoggerFactory
			.getLogger(XmlTalkController.class);

	@Autowired
	protected DiscussionXmlRepository discussionXmlRepository;

	@RequestMapping(value = "/products", method = RequestMethod.POST, consumes = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> createProduct(
			@RequestBody Discussion Discussion, UriComponentsBuilder builder) {
		discussionXmlRepository.add(Discussion);

		HttpHeaders headers = new HttpHeaders();
//		headers.setLocation(builder.path("/auth/{id}.xml")
//				.buildAndExpand(Discussion.getSku()).toUri());

		return new ResponseEntity<>("", headers, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/auth/{sku}.xml", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteProduct(@PathVariable("sku") Long sku) {
		discussionXmlRepository.remove(null);
	}

//	@RequestMapping(value = "/auth/{sku}.xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
//	public Discussion readProduct(@PathVariable("sku") Long sku) {
//		return discussionXmlRepository.findBySku(sku);
//	}

	@RequestMapping(value = "/products.xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_XML_VALUE)
	public List<Discussion> searchProducts(
			@RequestParam(required = false, value = "name") String name) {
		if (StringUtils.isEmpty(name)) {
			logger.info("Lookup all {} products...",
					discussionXmlRepository.count());
			return discussionXmlRepository.findAll();
		} else {
			logger.info("Lookup products by name: {}", name);
			return discussionXmlRepository.findByQuery(name);
		}
	}
}
