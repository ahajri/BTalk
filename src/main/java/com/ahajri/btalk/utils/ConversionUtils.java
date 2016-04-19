package com.ahajri.btalk.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.json.XML;

import com.ahajri.btalk.data.domain.converter.MapEntryConverter;
import com.marklogic.client.io.InputStreamHandle;
import com.thoughtworks.xstream.XStream;

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
	 * 
	 * @param xml:
	 *            XML Input Character
	 * @return: JSON formatted Character
	 */
	public static final String xml2Json(String xml) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String jsonPrettyPrintString = xmlJSONObj.toString(4);
		return jsonPrettyPrintString;
	}

	/**
	 * Convert Map to XML
	 * 
	 * @param map:
	 *            data {@link Map}
	 * @param rootName:
	 *            name of root XML node
	 * @return XML converted data
	 */
	public static final String getXml(Map map, String rootName) {
		XStream magicApi = new XStream();
		magicApi.registerConverter(new MapEntryConverter());
		magicApi.alias(rootName, Map.class);
		return magicApi.toXML(map);
	}

	/**
	 * get XML from {@link InputStreamHandle}
	 * 
	 * @param ish
	 *            {@link InputStreamHandle}
	 * @return XML formatted data
	 * @throws IOException
	 */
	public static final String getXml(InputStreamHandle ish) throws IOException {
		return IOUtils.toString(ish.get(), Charset.defaultCharset());
	}

	
}
