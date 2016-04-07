package com.ahajri.btalk.utils;

public enum Constants {

	COLLECTIONS_NAMES("colNames");

	private String key;

	private Constants(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}
