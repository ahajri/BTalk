package com.ahajri.btalk.data.domain.converter;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
	@SuppressWarnings({ "rawtypes" })
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
					if (o instanceof LinkedHashMap) {
						LinkedHashMap m = (LinkedHashMap) o;
						if (m.toString().contains("member_id")) {
							createNode(writer, m, "member");
						}
					} else {
						writer.setValue(o.toString());
					}
				}
			} else if (val instanceof Map) {
				Map mm = (Map) val;
				if (mm.toString().contains("content-type")) {
					createNode(writer, mm);
				} else if (mm.toString().contains("remoteHost")) {
					createNode(writer, mm);
				}
			} else if (null != val) {
				writer.setValue(val.toString());
			}
			writer.endNode();
		}
		writer.close();
	}

	private void createNode(HierarchicalStreamWriter writer, Map m) {
		createNode(writer, m, null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createNode(HierarchicalStreamWriter writer, Map m, String nodeName) {
		if (nodeName != null) {
			writer.startNode(nodeName);
		}
		for (Iterator<Entry<String, Object>> iterator = m.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, Object> e = iterator.next();
			String key = e.getKey();
			Object value = e.getValue();
			if (value instanceof Map) {
				createNode(writer, (Map) value, key);
			} else {
				writer.startNode(key);
				if (value == null) {
					writer.setValue("");
				} else {
					writer.setValue(String.valueOf(value));
				}
				writer.endNode();
			}
		}
		if (nodeName != null) {
			writer.endNode();
		}
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Map<String, String> map = new HashMap<String, String>();

		while (reader.hasMoreChildren()) {
			reader.moveDown();
			System.out.println("Node Name: " + reader.getNodeName());
			String key = reader.getNodeName();
			String value = reader.getValue();
			System.out.println(key + "######v######" + value);
			map.put(key, value);
			reader.moveUp();
		}
		return map;
	}

}
