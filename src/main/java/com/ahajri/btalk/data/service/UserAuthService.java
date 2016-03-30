package com.ahajri.btalk.data.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.DiscussionMember;
import com.ahajri.btalk.data.repository.DiscussionJsonRepository;

@Service("userService")
public class UserAuthService extends AService<DiscussionMember> {

	/** Logger */
	private final Logger LOGGER = Logger.getLogger(getClass());


	@Autowired
	DiscussionJsonRepository discussionJsonRepository;

	@Override
	public DiscussionMember create(DiscussionMember model) {
		LOGGER.info("Create discussion JSOn file: "+model.toString());
		
		return model;
	}

	@Override
	public Integer remove(DiscussionMember model) throws Exception {
		Integer count = Integer.parseInt("0");
		
		return count;
	}

	@Override
	public Integer modifyAll(DiscussionMember query, DiscussionMember update) throws Exception {
		return null;
	}

	@Override
	public Integer modify(DiscussionMember query, DiscussionMember update, boolean upsert,
			boolean multi) throws Exception {
		return null;
	}

	@Override
	public List<DiscussionMember> findAll() {
		return null;
	}

	@Override
	public List<DiscussionMember> search(String q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DiscussionMember> findByQuery(String q) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void replaceInsert(DiscussionMember model, String fragment) {
		// TODO Auto-generated method stub
		
	}

}
