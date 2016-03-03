package com.ahajri.btalk.utils;

public enum DiscussStatus {

	BUSY("BUSY"), ONLINE("ONLINE"), ABSENT("ABSENT"), OFFLINE("OFFLINE");

	private String value;

	private DiscussStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
