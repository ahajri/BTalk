package com.ahajri.btalk.data.domain.converter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author
 *         <p>
 *         ahajri
 *         </p>
 */
public class MapEntryConverter implements Converter {

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class clazz) {
		return AbstractMap.class.isAssignableFrom(clazz);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thoughtworks.xstream.converters.Converter#marshal(java.lang.Object,
	 * com.thoughtworks.xstream.io.HierarchicalStreamWriter,
	 * com.thoughtworks.xstream.converters.MarshallingContext)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		AbstractMap map = (AbstractMap) value;
		for (Object obj : map.entrySet()) {
			Map.Entry entry = (Map.Entry) obj;
			writer.startNode(entry.getKey().toString());
			Object val = entry.getValue();
			if (val instanceof List) {
				List l = (List) val;
				for (Object o : l) {
					if (o instanceof Map) {

						Map m = (Map) o;

						if (m.toString().contains("member_id")) {
							writer.startNode("member");
							for (@SuppressWarnings("unchecked")
							Iterator<Entry<String, Object>> iterator = m.entrySet().iterator(); iterator.hasNext();) {
								Entry<String, Object> e = iterator.next();
								writer.startNode(e.getKey());
								writer.setValue(e.getValue().toString());
								writer.endNode();
							}
							writer.endNode();
						}
						//TODO: check appropriate id name
						
					} else {
						writer.setValue(o.toString());
					}
				}
			} else if (null != val) {
				writer.setValue(val.toString());
			}
			writer.endNode();
		}

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<String, String> map = new HashMap<String, String>();
		while (reader.hasMoreChildren()) {
			reader.moveDown();
			String key = reader.getNodeName(); 
			String value = reader.getValue();
			System.out.println("value class: "+value.getClass());
			map.put(key, value);
			reader.moveUp();
		}
		return map;
	}

}
