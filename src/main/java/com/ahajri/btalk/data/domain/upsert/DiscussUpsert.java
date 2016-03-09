package com.ahajri.btalk.data.domain.upsert;

import com.ahajri.btalk.data.domain.Discussion;


public class DiscussUpsert {

	private Discussion model;
	private String fragment;

	public DiscussUpsert() {
		super();
	}

	public DiscussUpsert(Discussion model, String fragment) {
		super();
		this.model = model;
		this.fragment = fragment;
	}

	public Discussion getModel() {
		return model;
	}

	public void setModel(Discussion model) {
		this.model = model;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

}
