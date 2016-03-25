package com.ahajri.btalk.data.repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.domain.UserDiscussions;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.Format;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.RawQueryByExampleDefinition;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.query.StructuredQueryBuilder;
import com.marklogic.client.query.StructuredQueryDefinition;

/**
 * Sample implementation of the {@link IRepository} making use of MarkLogic's
 * {@link JSONDocumentManager}.
 *
 * @author <b>ahajri</b>
 */
@Component("discussionJsonRepository")
public class DiscussionJsonRepository implements IRepository<Discussion> {

	private static final Logger LOGGER = Logger
			.getLogger(DiscussionJsonRepository.class);

	protected static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddhhmmss");

	public static final String DISCUSSION_COLLECTION = "/DiscussionCollection";
	
	private static final String DISCUSS_DIR = "/discuss/";
	// TODO: use later
	public static final String OPTIONS_NAME = "price-year-bucketed";

	public static final int PAGE_SIZE = 10;
	
	@Autowired
	DatabaseClient databaseClient;

	@Autowired
	protected QueryManager queryManager;

	@Autowired
	protected JSONDocumentManager jsonDocumentManager;

	@Autowired
	protected StructuredQueryBuilder queryBuilder;

	@Autowired
	protected QueryOptionsManager queryOptionsManager;

	@Autowired
	protected UserDiscussionsJsonRepository userDiscussionsJsonRepository;

	@Override
	public Discussion persist(Discussion model) {
		// Add this document to a dedicated collection for later retrieval
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		Iterator<String> iterator = metadata.getCollections().iterator();
		boolean alreadyExists = false;
		loop: while (iterator.hasNext()) {
			String collectionName = iterator.next();
			if (collectionName.equals(DISCUSSION_COLLECTION)) {
				alreadyExists = true;
				break loop;
			}
		}
		if (!alreadyExists) {
			metadata.getCollections().add(DISCUSSION_COLLECTION);
		}
		model.setStartTime(new Date(System.currentTimeMillis()));
		String docName = "discussions__" + model.getId() + ".json";
		// Ensure User discussion document already exists
		List<UserDiscussions> found = userDiscussionsJsonRepository.findById(model
				.getId() + "_parent");
		UserDiscussions discussionsDoc = null;
		LOGGER.info("UserDiscussions found: " + found.size());
		if (CollectionUtils.isNotEmpty(found)) {
			// doc already exists
			discussionsDoc = found.get(0);
			Gson gson = new Gson();
			System.out.println(gson.toJson(model));
			List<UserDiscussions> openDiscussFound = userDiscussionsJsonRepository.searchByExample("{ \"endTime\": null }");
			if (CollectionUtils.isNotEmpty(openDiscussFound)) {
				//open discussion already exists
				discussionsDoc = openDiscussFound.get(0);
				
				return findByQuery(discussionsDoc.getDiscussions().getId()).get(0);
				
			}else{
				//create new user discussion file
				userDiscussionsJsonRepository.replaceInsert(discussionsDoc, gson.toJson(model));
			}
			
		} else {
			// create new one
			discussionsDoc = new UserDiscussions();
			discussionsDoc
					.setCreationDate(new Date(System.currentTimeMillis()));
			discussionsDoc.setId(model.getId() + "_parent");
			discussionsDoc.getDiscussions().add(model);
			JacksonHandle writeHandle = new JacksonHandle();
			JsonNode writeDocument = writeHandle.getMapper().convertValue(discussionsDoc,
					JsonNode.class);
			writeHandle.set(writeDocument);
			StringHandle stringHandle = new StringHandle(writeDocument.toString());
			//
			jsonDocumentManager
					.write(DISCUSS_DIR + docName, metadata, stringHandle);
		}
		databaseClient.release();
		return findByQuery(model.getId()).get(0);
	}

	@Override
	public void remove(Discussion model) {
		jsonDocumentManager.delete("");
	}

	/**
	 * Demonstrates End-to-End JSON direct access.
	 */
	public JsonNode getById(Discussion model) {
		JacksonHandle jacksonHandle = new JacksonHandle();
		jsonDocumentManager.read(getDocId(model), jacksonHandle);
		return jacksonHandle.get();
	}

	@Override
	public Long count() {
		StructuredQueryBuilder sb = queryManager.newStructuredQueryBuilder();
		StructuredQueryDefinition criteria = sb
				.collection(DISCUSSION_COLLECTION);
		SearchHandle resultsHandle = new SearchHandle();
		queryManager.search(criteria, resultsHandle);
		return resultsHandle.getTotalResults();
	}

	@Override
	public List<Discussion> findAll() {
		StringQueryDefinition queryDef = queryManager
				.newStringDefinition(OPTIONS_NAME);
		queryDef.setCollections(DISCUSSION_COLLECTION);
		SearchHandle resultsHandle = new SearchHandle();
		queryManager.setPageLength(PAGE_SIZE);
		queryManager.search(queryDef, resultsHandle, 0);
		return toSearchResult(resultsHandle);
	}

	@Override
	public List<Discussion> findByQuery(String q) {
		// KeyValueQueryDefinition query = queryManager.newKeyValueDefinition();
		// query.put(queryManager.newKeyLocator("name"), name); // exact match
		// Alternatively use:
		StringQueryDefinition query = queryManager.newStringDefinition();
		// query.setCriteria("\"Aretha Franklin\" AND \"Otis Redding\"");
		query.setCriteria(q); // example: "index OR Cassel NEAR Hare"
		query.setCollections(DISCUSSION_COLLECTION);
		queryManager.setPageLength(PAGE_SIZE);
		SearchHandle resultsHandle = new SearchHandle();
		return toSearchResult(queryManager.search(query, resultsHandle));
	}

	// ~~

	private String getDocId(Discussion model) {
		return String.format("/discuss/discussion_%d.json", model.getId());
	}

	private List<Discussion> toSearchResult(SearchHandle resultsHandle) {
		List<Discussion> models = new ArrayList<Discussion>();
		for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
			LOGGER.info("  * found {}" + summary.getUri());
			// Assumption: summary URI refers to JSON document
			JacksonHandle jacksonHandle = new JacksonHandle();
			jsonDocumentManager.read(summary.getUri(), jacksonHandle);
			models.add(fetchDiscussion(jacksonHandle));
		}
		return models;
	}

	/**
	 * 
	 * @param jacksonHandle
	 *            {@link JacksonHandle}
	 * @return {@link Discussion}
	 */
	private Discussion fetchDiscussion(JacksonHandle jacksonHandle) {
		try {
			JsonNode jsonNode = jacksonHandle.get();
			return jacksonHandle.getMapper().readValue(jsonNode.toString(),
					Discussion.class);
		} catch (IOException e) {
			throw new RuntimeException("Unable to cast to Discussion", e);
		}
	}

	@Override
	public Discussion findOne(Object... params) {
		SearchHandle resultsHandle = queryManager.search(
				queryBuilder.directory(true, DISCUSS_DIR), new SearchHandle());

		MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
		for (MatchDocumentSummary docSummary : docSummaries) {
			InputStreamHandle docHandle = jsonDocumentManager.read(
					docSummary.getUri(), new InputStreamHandle());

		}
		return null;
	}

	@Override
	public void update(Discussion model) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
				.newPatchBuilder();
		DocumentPatchHandle xmlPatch = jsonPatchBldr.insertFragment(
				DISCUSS_DIR, Position.LAST_CHILD, "added:new data").build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);
	}

	@Override
	public void replaceInsert(Discussion model, String fragment) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
				.newPatchBuilder();
		DocumentPatchHandle xmlPatch = jsonPatchBldr.replaceInsertFragment(
				Discussion.docName, DISCUSS_DIR, Position.LAST_CHILD, fragment)
				.build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);

	}

	@Override
	public List<Discussion> searchByExample(String example) {
		String rawXMLQuery = "{\"$query\": { \"id\": \"jleogmail\" }}";
		StringHandle qbeHandle = new StringHandle(rawXMLQuery)
				.withFormat(Format.JSON);
		RawQueryByExampleDefinition query = queryManager
				.newRawQueryByExampleDefinition(qbeHandle);
		SearchHandle resultsHandle = queryManager.search(query,
				new SearchHandle());
		return toSearchResult(resultsHandle);
	}

	@Override
	public List<Discussion> findById(String id) {
		String rawXMLQuery = "{ \"$query\": { \"id\": \"" + id + "\" } }";
		StringHandle qbeHandle = new StringHandle(rawXMLQuery)
				.withFormat(Format.JSON);
		RawQueryByExampleDefinition query = queryManager
				.newRawQueryByExampleDefinition(qbeHandle);
		SearchHandle resultsHandle = queryManager.search(query,
				new SearchHandle());
		query.setCollections(DISCUSSION_COLLECTION);
		queryManager.setPageLength(PAGE_SIZE);
		return toSearchResult(queryManager.search(query, resultsHandle));
	}
}
