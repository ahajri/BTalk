package com.ahajri.btalk.data.domain.mapper;

import org.apache.commons.beanutils.DynaBean;
import org.apache.log4j.Logger;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * Class used for mapping given JSON data in {@link DynaBean} Object
 * 
 * @author
 *         <p>
 *         ahajri
 *         </p>
 *
 */
public class JsonDynaMapper {

	/** LOGGER */
	private final static Logger LOGGER = Logger.getLogger(JsonDynaMapper.class);

	/**
	 * Method to Map JSON Input data into dynaBean
	 * 
	 * @param json:
	 *            json data, example:
	 *            <blockquote>{"objectName":{"key1":"...}}</blockquote>
	 * @return Mapped {@link DynaBean}Object
	 */
	public static DynaBean mapDynaBean(String json) {
		LOGGER.debug("Converting json to bean: " + json);
		JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(json);
		return (DynaBean) JSONSerializer.toJava((JSON) jsonObject);
	}
}
