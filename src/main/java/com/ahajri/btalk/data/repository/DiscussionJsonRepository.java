package com.ahajri.btalk.data.repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahajri.btalk.data.domain.Discussion;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.MarkLogicIOException;
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

	private static final Logger LOGGER = Logger.getLogger(DiscussionJsonRepository.class);

	protected static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

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

	@Override
	public void persist(Discussion model, DocumentMetadataHandle metadata) {

		String docName = "discussion__" + model.getId() + ".json";

		// List<UserDiscussions> openDiscussFound =
		// userDiscussionsJsonRepository.searchByExample("{ \"endTime\": null
		// }");

		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(model, JsonNode.class);
		writeHandle.set(writeDocument);
		StringHandle stringHandle = new StringHandle(writeDocument.toString());
		jsonDocumentManager.write(DISCUSS_DIR + docName, metadata, stringHandle);

		// databaseClient.release();

	}

	@Override
	public boolean remove(Discussion model) {
		//FIXME: search document
		String q = "{\"id\":\""+model.getId()+"\"}";
		System.out.println(q);
		List<Discussion> found = searchByExample(q);
		if (CollectionUtils.isNotEmpty(found) && found.size()==1) {
			jsonDocumentManager.delete(getDocId(found.get(0)));
			if(CollectionUtils.isEmpty(searchByExample(q))){
				return true;
			}else{
				return false;
			}
		}
		return false;
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
		StructuredQueryDefinition criteria = sb.collection(DISCUSSION_COLLECTION);
		SearchHandle resultsHandle = new SearchHandle();
		queryManager.search(criteria, resultsHandle);
		return resultsHandle.getTotalResults();
	}

	@Override
	public List<Discussion> findAll() {
		StringQueryDefinition queryDef = queryManager.newStringDefinition(OPTIONS_NAME);
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
		return String.format("/discuss/discussion__%s.json", model.getId());
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
			return jacksonHandle.getMapper().readValue(jsonNode.toString(), Discussion.class);
		} catch (IOException e) {
			throw new RuntimeException("Unable to cast to Discussion", e);
		}
	}

	@Override
	public Discussion findOne(Object... params) {
		SearchHandle resultsHandle = queryManager.search(queryBuilder.directory(true, DISCUSS_DIR), new SearchHandle());

		MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
		for (MatchDocumentSummary docSummary : docSummaries) {
			InputStreamHandle docHandle = jsonDocumentManager.read(docSummary.getUri(), new InputStreamHandle());

		}
		return null;
	}

	@Override
	public void update(Discussion model) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager.newPatchBuilder();
		// :FIXME
		DocumentPatchHandle xmlPatch = jsonPatchBldr.insertFragment(DISCUSS_DIR, Position.LAST_CHILD, "added:new data")
				.build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);
	}

	@Override
	public void replaceInsert(Discussion model, String fragment) {

		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager.newPatchBuilder();
		DocumentPatchHandle jsonPatch = jsonPatchBldr
				.insertFragment(/* DISCUSS_DIR + model.getDocName() , */
						DISCUSS_DIR + model.getDocName(), Position.LAST_CHILD, fragment)
				.build();
		jsonDocumentManager.patch(getDocId(model), jsonPatch);

	}

	// public void insertFragment(Discussion model, String fragment) {
	// DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
	// .newPatchBuilder();
	// DocumentPatchHandle jsonPatch = jsonPatchBldr
	// .insertFragment(DISCUSS_DIR + model.getDocName(),
	// Position.LAST_CHILD, fragment).build();
	// jsonDocumentManager.patch(getDocId(model), jsonPatch);
	//
	// }

	@Override
	public List<Discussion> searchByExample(String q) {
		// String jsonQuery = "{\"$query\": { \"id\": \"jleogmail\" }}";
		String jsonQuery = "{\"$query\": " + q + "}";
		StringHandle qbeHandle = new StringHandle(jsonQuery).withFormat(Format.JSON);
		RawQueryByExampleDefinition query = queryManager.newRawQueryByExampleDefinition(qbeHandle);
		SearchHandle resultsHandle = queryManager.search(query, new SearchHandle());
		queryManager.setPageLength(10);
		return toSearchResult(resultsHandle);
	}

	@Override
	public List<Discussion> findById(String id) {
		String rawXMLQuery = "{ \"$query\": { \"id\": \"" + id + "\" } }";
		StringHandle qbeHandle = new StringHandle(rawXMLQuery).withFormat(Format.JSON);
		RawQueryByExampleDefinition query = queryManager.newRawQueryByExampleDefinition(qbeHandle);
		SearchHandle resultsHandle = queryManager.search(query, new SearchHandle());
		query.setCollections(DISCUSSION_COLLECTION);
		queryManager.setPageLength(PAGE_SIZE);
		return toSearchResult(queryManager.search(query, resultsHandle));
	}

	@Override
	public void persist(Discussion model) throws Exception {
		String docName = model.getDocName();// "discussion__" + model.getId() +
											// ".json";
		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(model, JsonNode.class);
		writeHandle.set(writeDocument);
		StringHandle stringHandle = new StringHandle(writeDocument.toString());
		jsonDocumentManager.write(DISCUSS_DIR + docName, new DocumentMetadataHandle(), stringHandle);
		// databaseClient.release();

	}
}
