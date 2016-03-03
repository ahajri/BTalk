package com.ahajri.btalk.utils;

public enum DiscussRole {

	DISCUSS_CREATOR("DISCUSS_CREATOR"), DISCUSS_MEMBER("DISCUSS_MEMBER"), DISCUSS_HUNTER(
			"DISCUSS_HUNTER");

	private String value;

	private DiscussRole(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
