package com.ahajri.btalk.data.domain.mapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.log4j.Logger;

import com.ahajri.btalk.data.domain.xml.XmlMap;
import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import net.sf.ezmorph.bean.MorphDynaBean;
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
public class DataMapper {

	/** LOGGER */
	private final static Logger LOGGER = Logger.getLogger(DataMapper.class);
	private static final XStream xstream = new XStream(new StaxDriver());

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

	/**
	 * 
	 * @param json
	 * @return
	 */
	public static HashMap<String, Object> json2Map(Object json) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONObject jObject = (JSONObject) json;
		Iterator<?> keys = jObject.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object value = jObject.get(key);
			map.put(key, value);

		}
		return map;
	}

	/**
	 * Transform JSON Object to XML
	 * 
	 * @param jsonObject
	 *            JSON Object
	 * @return XML mapped data
	 * @throws Exception
	 */
	public static String json2Xml(Object jsonObject) throws Exception {
		LOGGER.debug("Converting json Object to XML: " + jsonObject);
		DynaBean bean = mapDynaBean(new Gson().toJson(jsonObject));
		DynaProperty[] dynaProps = bean.getDynaClass().getDynaProperties();
		for (DynaProperty dynaProp : dynaProps) {
			System.out.println("#########" + dynaProp.getName() + " = " + bean.get(dynaProp.getName()));
		}
		for (XmlMap map : convertDynaBeanListToArrayList(Arrays.asList(bean))) {
			Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, Object> entry = iterator.next();
				String key = entry.getKey();
				Object value = entry.getValue();
				if (value instanceof List) {
					List listField = ((List) value);
					for (Object f : listField) {
						if (f instanceof MorphDynaBean) {
							MorphDynaBean mBean = (MorphDynaBean) f;

							for (DynaProperty dynaProperty : dynaProps) {

								String propName = dynaProperty.getName();
								System.out.println("#propName#" + propName + "#value#" + mBean.get(propName));
							}

						}
					}

				} else if (value instanceof String) {
					for (DynaProperty dynaProperty : dynaProps) {

					}
				}
			}
		}
		return xstream.toXML(jsonObject);
	}

	public static ArrayList<XmlMap> convertDynaBeanListToArrayList(List<DynaBean> theList) {
		ArrayList<XmlMap> result = new ArrayList<XmlMap>();
		DynaProperty[] dynaProperties = null;
		for (Integer i = 0; i < theList.size(); i++) {
			DynaBean row = theList.get(i);
			XmlMap resultRow = new XmlMap();
			// each raw got the same column names, no need to fetch this for
			// every line
			if (dynaProperties == null) {
				dynaProperties = row.getDynaClass().getDynaProperties();
			}
			for (Integer j = 0; j < dynaProperties.length; j++) {
				String columnName = dynaProperties[j].getName();
				resultRow.put(columnName, row.get(columnName));
			}
			result.add(resultRow);
		}

		return result;
	}
}
