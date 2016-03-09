package com.ahajri.btalk.data.domain.upsert;

import java.io.Serializable;

public abstract class AUpsert<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8137760098892950901L;

	private T model;
	private String fragment;

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

	public String getFragment() {
		return fragment;
	}

	public void setFragment(String fragment) {
		this.fragment = fragment;
	}

}
