package com.ahajri.btalk.data.domain;

import java.io.Serializable;
import java.util.HashSet;

import com.ahajri.btalk.utils.ActionResultName;

public class ActionResult implements Serializable {

	/**
	 * Serialization UID
	 */
	private static final long serialVersionUID = 2215562584560868453L;

	private int code;
	private final HashSet<String> messages = new HashSet<String>();
	private ActionResultName actionResultName;
	private String jsonReturnData;

	public ActionResult() {
		super();
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public HashSet<String> getMessages() {
		return messages;
	}

	public ActionResultName getActionResultName() {
		return actionResultName;
	}

	public void setActionResultName(ActionResultName actionResultName) {
		this.actionResultName = actionResultName;
	}

	public String getJsonReturnData() {
		return jsonReturnData;
	}

	public void setJsonReturnData(String jsonReturnData) {
		this.jsonReturnData = jsonReturnData;
	}

}
