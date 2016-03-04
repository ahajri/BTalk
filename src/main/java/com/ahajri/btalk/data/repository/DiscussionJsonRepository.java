package com.ahajri.btalk.data.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ahajri.btalk.data.domain.Discussion;
import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.document.DocumentPatchBuilder;
import com.marklogic.client.document.DocumentPatchBuilder.Position;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
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

	public static final String COLLECTION_REF = "/DiscussionCollection";
	// TODO: later
	public static final String OPTIONS_NAME = "price-year-bucketed";
	
	public static final int PAGE_SIZE = 10;

	@Autowired
	protected QueryManager queryManager;

	@Autowired
	protected JSONDocumentManager jsonDocumentManager;

	@Override
	public void add(Discussion discussion) {
		// Add this document to a dedicated collection for later retrieval
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		Iterator<String> iterator = metadata.getCollections().iterator();

		metadata.getCollections().add(COLLECTION_REF);

		JacksonHandle writeHandle = new JacksonHandle();
		JsonNode writeDocument = writeHandle.getMapper().convertValue(
				discussion, JsonNode.class);
		writeHandle.set(writeDocument);

		// TODO: writing JacksonHandle with metadata throws:
		// java.io.IOException: Attempted write to closed stream.
		StringHandle stringHandle = new StringHandle(writeDocument.toString());
		jsonDocumentManager.write("/discuss/discussion.json", metadata,
				stringHandle);
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
		query.setCriteria(q); // example: "index OR Cassel NEAR Hare"
		query.setCollections(COLLECTION_REF);

		queryManager.setPageLength(PAGE_SIZE);
		SearchHandle resultsHandle = new SearchHandle();
		queryManager.search(query, resultsHandle);
		return toSearchResult(resultsHandle);
	}

	// ~~

	private String getDocId(Discussion model) {
		return String.format("/discussion/%d.json", model.getIdentifier());
	}

	private List<Discussion> toSearchResult(SearchHandle resultsHandle) {
		List<Discussion> products = new ArrayList<Discussion>();
		for (MatchDocumentSummary summary : resultsHandle.getMatchResults()) {
			LOGGER.info("  * found {}" + summary.getUri());
			// Assumption: summary URI refers to JSON document
			JacksonHandle jacksonHandle = new JacksonHandle();
			jsonDocumentManager.read(summary.getUri(), jacksonHandle);
			products.add(fetchProduct(jacksonHandle));
		}
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Discussion model) {
		DocumentPatchBuilder xmlPatchBldr = jsonDocumentManager.newPatchBuilder();
		DocumentPatchHandle xmlPatch = 
		    xmlPatchBldr.insertFragment(
		        "/discuss", 
		        Position.LAST_CHILD,
		        "added:new data")
		      .build();
		jsonDocumentManager.patch(getDocId(model), xmlPatch);
	}
}
