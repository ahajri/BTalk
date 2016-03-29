package com.ahajri.btalk.data.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;
import com.marklogic.client.io.DocumentMetadataHandle;

@Service
public class DiscussionService extends AService<Discussion> {

	@Autowired
	DiscussionJsonRepository discussionJsonRepository;

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
		// TODO: Step 1 _ create discussion

		// TODO: create _ return created discussion
		return null;
	}

	@Override
	public Integer remove(Discussion model) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer modifyAll(Discussion query, Discussion update)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer modify(Discussion query, Discussion update, boolean upsert,
			boolean multi) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Discussion> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Discussion> search(String q) {
		// TODO Auto-generated method stub
		return null;
	}

}
