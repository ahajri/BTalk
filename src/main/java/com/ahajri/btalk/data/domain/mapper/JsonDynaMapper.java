package com.ahajri.btalk.data.domain.mapper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BasicDynaClass;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

	/**
	 * Method to Map JSON Input data into dynaBean
	 * 
	 * @param jsonInput:
	 *            JSON input data
	 * @param objectName:
	 *            data json key
	 *            <blockquote>{"objectName":{"key1":"...}}</blockquote>
	 * @return Mapped {@link DynaBean}Object
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static DynaBean mapDynaBean(String jsonInput, String objectName)
			throws IllegalAccessException, InstantiationException {
		Gson gson = new Gson();
		DynaClass dynaClass = null;
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		Map<String, Object> map = gson.fromJson(jsonInput, type);
		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();
		List<DynaProperty> dynaProps = new ArrayList<DynaProperty>();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = iterator.next();
			dynaProps.add(new DynaProperty(entry.getKey(), entry.getValue().getClass()));
		}
		dynaClass = new BasicDynaClass(objectName, null, ((DynaProperty[]) dynaProps.toArray()));
		return dynaClass.newInstance();
	}
}
