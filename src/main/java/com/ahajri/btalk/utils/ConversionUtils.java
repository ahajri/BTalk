package com.ahajri.btalk.utils;

import org.json.JSONObject;
import org.json.XML;

/**
 * Conversion Class Utils
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 *
 */
public class ConversionUtils {

	/**
	 * Convert XML to JSON
	 * @param xml: XML Input Character
	 * @return: JSON formatted Character
	 */
	public static final String xml2Json(String xml) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String jsonPrettyPrintString = xmlJSONObj.toString(4);
		return jsonPrettyPrintString;
	}

}
