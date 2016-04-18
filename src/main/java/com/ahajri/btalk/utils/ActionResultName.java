package com.ahajri.btalk.utils;

/**
 * Enum for Service action result
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 *
 */
public enum ActionResultName {

	SUCCESSFULL("S"), FAIL("F"), SUCCESSFULL_WITH_WARNING("SW");

	private String name;

	private ActionResultName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
