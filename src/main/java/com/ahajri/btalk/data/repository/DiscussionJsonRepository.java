package com.ahajri.btalk.data.repository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.domain.DiscussionMember;
import com.ahajri.btalk.utils.DiscussRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.admin.QueryOptionsManager;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.io.marker.DocumentPatchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
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

	public static final String COLLECTION_REF = "/DiscussionCollection";
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
	public Discussion add(Discussion model) {
		// Add this document to a dedicated collection for later retrieval

		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		Iterator<String> iterator = metadata.getCollections().iterator();
		boolean alreadyExists = false;
		loop: while (iterator.hasNext()) {
			String collectionName = iterator.next();
			if (collectionName.equals(COLLECTION_REF)) {
				alreadyExists = true;
				break loop;
			}
		}
		if (!alreadyExists) {
			metadata.getCollections().add(COLLECTION_REF);
		}

		// check if document has Id ?

		String currentDate = sdf.format(new Date(System.currentTimeMillis()));
		model.setStartTime(currentDate);

		String docName = "discussion_" + currentDate + ".json";

		if (model.getId() == null) {
			for (DiscussionMember member : model.getMembers()) {
				if (member.getDiscussRole().equalsIgnoreCase(
						DiscussRole.DISCUSS_CREATOR.getValue())) {
					String identity = member.getId() + sdf.format(new Date());
					model.setId(identity);
				}
			}
		}

		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(model,
				JsonNode.class);
		writeHandle.set(writeDocument);
		// TODO: writing JacksonHandle with metadata throws:
		StringHandle stringHandle = new StringHandle(writeDocument.toString());
		jsonDocumentManager
				.write("/discuss/" + docName, metadata, stringHandle);
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
		StructuredQueryDefinition criteria = sb.collection(COLLECTION_REF);
		SearchHandle resultsHandle = new SearchHandle();
		queryManager.search(criteria, resultsHandle);
		return resultsHandle.getTotalResults();
	}

	@Override
	public List<Discussion> findAll() {
		StringQueryDefinition queryDef = queryManager
				.newStringDefinition(OPTIONS_NAME);
		queryDef.setCollections(COLLECTION_REF);

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
		query.setCollections(COLLECTION_REF);
		queryManager.setPageLength(PAGE_SIZE);
		SearchHandle resultsHandle = new SearchHandle();
		return toSearchResult(queryManager.search(query, resultsHandle));
	}

	// ~~

	private String getDocId(Discussion model) {
		return String.format("/discussion/%d.json", model.getId());
	}

	private List<Discussion> toSearchResult(SearchHandle resultsHandle) {
		List<Discussion> models = new ArrayList<Discussion>();
		for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
			LOGGER.info("  * found {}" + summary.getUri());
			// Assumption: summary URI refers to JSON document
			JacksonHandle jacksonHandle = new JacksonHandle();
			jsonDocumentManager.read(summary.getUri(), jacksonHandle);
			models.add(fetchProduct(jacksonHandle));
		}
		return models;
	}

	/**
	 * 
	 * @param jacksonHandle
	 *            {@link JacksonHandle}
	 * @return {@link Discussion}
	 */
	private Discussion fetchProduct(JacksonHandle jacksonHandle) {
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
		// FIXME: list all documents
		// check if discuss.json already exist
		SearchHandle resultsHandle = queryManager.search(
				queryBuilder.directory(true, "/discuss/"), new SearchHandle());

		MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
		for (MatchDocumentSummary docSummary : docSummaries) {
			System.out.println("#########" + docSummary.getUri());
			InputStreamHandle docHandle = jsonDocumentManager.read(
					docSummary.getUri(), new InputStreamHandle());

		}
		return null;
	}

	@Override
	public void update(Discussion model) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
				.newPatchBuilder();
		DocumentPatchHandle xmlPatch = jsonPatchBldr.insertFragment("/discuss",
				Position.LAST_CHILD, "added:new data").build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);
	}

	@Override
	public void replaceInsert(Discussion model, String fragment) {
		DocumentPatchBuilder jsonPatchBldr = jsonDocumentManager
				.newPatchBuilder();
		DocumentPatchHandle xmlPatch = jsonPatchBldr.replaceInsertFragment(
				Discussion.docName, "/discuss/", Position.LAST_CHILD, fragment)
				.build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);

	}
}
