package com.ahajri.btalk.data.service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.discuss.Discussion;
import com.ahajri.btalk.data.domain.discuss.DiscussionUpsert;
import com.ahajri.btalk.data.domain.discuss.Message;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;
import com.google.gson.Gson;
import com.marklogic.client.io.DocumentMetadataHandle;

/**
 * @author
 * 		<p>
 *         ahajri
 *         </p>
 */
@Service
public class DiscussionService extends AService<Discussion> {

	private Gson gson = new Gson();
	@Autowired
	private DiscussionJsonRepository discussionJsonRepository;

	@Override
	public Discussion create(Discussion model) {
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
		discussionJsonRepository.persist(model, metadata);
		return findByQuery(model.getId()).get(0);
	}

	@Override
	public boolean remove(Discussion model)  {
		return discussionJsonRepository.remove(model);
	}

	@Override
	public Integer modifyAll(Discussion query, Discussion update) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer modify(Discussion query, Discussion update, boolean upsert, boolean multi) throws Exception {
		return null;
	}

	@Override
	public List<Discussion> findAll() {
		return discussionJsonRepository.findAll();
	}

	@Override
	public List<Discussion> search(String q) {
		return discussionJsonRepository.searchByExample(q);
	}

	@Override
	public List<Discussion> findByQuery(String q) {
		return discussionJsonRepository.findByQuery(q);
	}

	@Override
	public void replaceInsert(Discussion model, String fragment) {
		discussionJsonRepository.replaceInsert(model, fragment);
	}

	/**
	 * 
	 * @param model
	 * @return full {@link DiscussionUpsert}
	 * @throws Exception
	 */
	public Discussion addMessage(DiscussionUpsert model) throws Exception {
		List<Discussion> found = discussionJsonRepository
				.searchByExample("{ \"id\": \"" + model.getModel().getId() + "\" }");

		if (CollectionUtils.isNotEmpty(found) && found.size() == 1) {
			Discussion discuss = found.get(0);
			Message msg = new Message();
			msg.setAckited(false);
			msg.setCreationTime(new Timestamp(System.currentTimeMillis()));
			msg.setText(model.getFragment());
			discuss.getMessages().add(msg);
			discussionJsonRepository.replaceInsert(discuss, gson.toJson(msg));
			List<Discussion> modifieds = search("{ \"id\": \"" + discuss.getId() + "\" }");
			return modifieds.get(0);
		} else {
			throw new Exception("Discussion not found or multiple discussions found");
		}

	}

	public boolean deleteMessage(Discussion model) throws Exception {
		return discussionJsonRepository.remove(model);
	}

}
