package com.ahajri.btalk.data.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.DiscussionMember;

@Service("userService")
public class UserAuthService extends AService<DiscussionMember> {

	/** Logger */
	private final Logger LOGGER = Logger.getLogger(getClass());



	@Override
	public DiscussionMember persist(DiscussionMember model) {
		
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

}
