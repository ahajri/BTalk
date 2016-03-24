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

import com.ahajri.btalk.data.domain.UserDiscussions;
import com.fasterxml.jackson.databind.JsonNode;
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
import com.marklogic.client.query.KeyValueQueryDefinition;
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
@Component("discussionsJsonRepository")
public class DiscussionsJsonRepository implements IRepository<UserDiscussions> {

	private static final Logger LOGGER = Logger
			.getLogger(DiscussionsJsonRepository.class);

	protected static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyyMMddhhmmss");

	public static final String DISCUSSION_COLLECTION = "/DiscussionCollection";
	private static final String DISCUSS_DIR = "/discuss/";
	// TODO: later
	public static final String OPTIONS_NAME = "price-year-bucketed";

	public static final int PAGE_SIZE = 10;

	@Autowired
	protected QueryManager queryManager;

	@Autowired
	protected JSONDocumentManager jsonDocumentManager;

	@Autowired
	protected StructuredQueryBuilder queryBuilder;

	@Autowired
	protected QueryOptionsManager queryOptionsManager;
	
	

	@Override
	public UserDiscussions persist(UserDiscussions model) {
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

		// check if document has Id ?

		model.setCreationDate(new Date(System.currentTimeMillis()));

		String docName = "discussion_" + model.getId() + ".json";

		// Ensure document already exists
		List<UserDiscussions> found = this.findById(model.getId());
		System.out.println("#########"+found);
		System.exit(0);
		LOGGER.info("Discussions already exists: " + found.toString());
		if (CollectionUtils.isNotEmpty(found)) {
			for (UserDiscussions d : found) {
				System.out.println(d.getId());
			}
		}
		//
		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(model,
				JsonNode.class);
		writeHandle.set(writeDocument);
		StringHandle stringHandle = new StringHandle(writeDocument.toString());

		jsonDocumentManager
				.write(DISCUSS_DIR + docName, metadata, stringHandle);
		return findByQuery(model.getId()).get(0);
	}

	@Override
	public void remove(UserDiscussions model) {
		jsonDocumentManager.delete("");
	}

	/**
	 * Demonstrates End-to-End JSON direct access.
	 */
	public JsonNode getById(UserDiscussions model) {
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
	public List<UserDiscussions> findAll() {
		StringQueryDefinition queryDef = queryManager
				.newStringDefinition(OPTIONS_NAME);
		queryDef.setCollections(DISCUSSION_COLLECTION);
		SearchHandle resultsHandle = new SearchHandle();
		queryManager.setPageLength(PAGE_SIZE);
		queryManager.search(queryDef, resultsHandle, 0);
		return toSearchResult(resultsHandle);
	}

	@Override
	public List<UserDiscussions> findByQuery(String q) {
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
	
	@Override
	public List<UserDiscussions> findById(String id) {
		String rawXMLQuery = "{ \"$query\": { \"id\": \""+id+"\" } }";
		StringHandle qbeHandle = new StringHandle(rawXMLQuery).withFormat(Format.JSON);
		RawQueryByExampleDefinition query = queryManager.newRawQueryByExampleDefinition(qbeHandle);
		SearchHandle resultsHandle = queryManager.search(query, new SearchHandle());
		query.setCollections(DISCUSSION_COLLECTION);
		queryManager.setPageLength(PAGE_SIZE);
		return toSearchResult(queryManager.search(query, resultsHandle));
	}

	private String getDocId(UserDiscussions model) {
		return String.format("/Discussions/discussions_%d.json", model.getId());
	}

	private List<UserDiscussions> toSearchResult(SearchHandle resultsHandle) {
		List<UserDiscussions> models = new ArrayList<UserDiscussions>();
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
	 * @return {@link UserDiscussions}
	 */
	private UserDiscussions fetchDiscussion(JacksonHandle jacksonHandle) {
		try {
			JsonNode jsonNode = jacksonHandle.get();
			return jacksonHandle.getMapper().readValue(jsonNode.toString(),
					UserDiscussions.class);
		} catch (IOException e) {
			throw new RuntimeException("Unable to cast to Discussions", e);
		}
	}

	@Override
	public UserDiscussions findOne(Object... params) {
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
	public void update(UserDiscussions model) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
				.newPatchBuilder();
		DocumentPatchHandle xmlPatch = jsonPatchBldr.insertFragment(
				DISCUSS_DIR, Position.LAST_CHILD, "added:new data").build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);
	}

	@Override
	public void replaceInsert(UserDiscussions model, String fragment) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
				.newPatchBuilder();
		DocumentPatchHandle xmlPatch = jsonPatchBldr.replaceInsertFragment(
				"discussions_" + model.getId() + ".json", DISCUSS_DIR,
				Position.LAST_CHILD, fragment).build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);

	}

	@Override
	public List<UserDiscussions> searchByExample(String example) {
		String rawXMLQuery = "{\"$query\": { \"id\": \"jleogmail\" }}";
		StringHandle qbeHandle = new StringHandle(rawXMLQuery)
				.withFormat(Format.JSON);
		RawQueryByExampleDefinition query = queryManager
				.newRawQueryByExampleDefinition(qbeHandle);
		SearchHandle resultsHandle = queryManager.search(query,
				new SearchHandle());
		return toSearchResult(resultsHandle);
	}
}
