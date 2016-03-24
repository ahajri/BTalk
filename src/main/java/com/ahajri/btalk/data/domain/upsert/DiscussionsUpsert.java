package com.ahajri.btalk.data.domain.upsert;

import com.ahajri.btalk.data.domain.UserDiscussions;


public class DiscussionsUpsert  {

	private UserDiscussions model;
	
	private String fragment;
	
	public DiscussionsUpsert() {
		super();
	}

	public UserDiscussions getModel() {
		return model;
	}

	public void setModel(UserDiscussions model) {
		this.model = model;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

}
