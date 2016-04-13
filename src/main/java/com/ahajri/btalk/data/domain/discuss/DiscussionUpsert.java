package com.ahajri.btalk.data.domain.discuss;

/**
 * 
 * @author
 * 		<p>
 *         ahajri
 *         </p>
 *
 */
public class DiscussionUpsert {

	private Discussion model;
	private String fragment;

	public DiscussionUpsert() {
		super();
	}

	public DiscussionUpsert(Discussion model, String fragment) {
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