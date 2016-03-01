package com.ahajri.btalk.utils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Criteria;

import com.ahajri.btalk.data.domain.IModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * 
 * @author <p>
 *         ahajri
 *         </p>
 */
public class MongoUtils {
	private final static Logger LOGGER = Logger.getLogger(MongoUtils.class);

	public static void clearCollection(DBCollection collection) {
		long count = collection.count();
		LOGGER.info("Count Before: " + count);
		if (count > 0) {
			collection.remove(new BasicDBObject());
			LOGGER.info("Count After: " + collection.count());
		}
	}

	public static void createCollections(DB db, List<String> tables) {
		for (String tableName : tables) {
			DBObject options = new BasicDBObject("capped", false);
			db.createCollection(tableName, options);
		}
	}

	/**
	 * Convert Domain model beans to mongo basic object
	 * 
	 * @param model
	 *            {@link IModel}
	 * @return {@link BasicDBObject}
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static BasicDBObject model2DbObject(IModel model)
			throws IllegalArgumentException, IllegalAccessException {
		BasicDBObject obj = new BasicDBObject();
		Field[] fields = model.getClass().getDeclaredFields();
		for (Field f : fields) {
			String name = f.getName();

			Object v = f.get(model);
			if (v != null) {
				if (v instanceof Integer || v instanceof Short
						|| v instanceof Byte || v instanceof AtomicInteger) {
					obj.put(name, Integer.parseInt(String.valueOf(v)));
				} else if (v instanceof Long || v instanceof AtomicLong) {
					obj.put(name, (Long) v);
				} else if (v instanceof Double) {
					obj.put(name, (Double) v);
				} else if (v instanceof BigDecimal) {
					obj.put(name, ((BigDecimal) v).doubleValue());
				} else if (v instanceof Blob) {
					Blob blob = (Blob) v;
					try {
						int blobLength = (int) blob.length();
						byte[] blobAsBytes = blob.getBytes(1, blobLength);
						obj.put(name, blobAsBytes);
					} catch (Exception e) {
						LOGGER.error(e.getMessage());
					}
				} else {
					obj.put(name, v);
				}
			}

		}
		return obj;
	}

	/**
	 * convert {@link DBObject} to given {@link IModel}
	 * 
	 * @param _obj
	 *            : Mongo Object
	 * @param model
	 *            , {@link IModel}
	 * @return instantiated {@link IModel}
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static IModel dbObject2Model(DBObject _obj, IModel model)
			throws Exception {
		if (_obj != null && model != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			for (String key : _obj.keySet()) {
				if (key.equals("id")) {
					key = "_id";
				}
				map.put(key, _obj.get(key));
			}
			Field[] fieldsTab = model.getClass().getDeclaredFields();
			List<Field> fields = Arrays.asList(fieldsTab);
			for (Field f : fields) {
				String name = f.getName();
				Object v = map.get(name);
				if (v instanceof Integer || v instanceof Short
						|| v instanceof Byte || v instanceof AtomicInteger) {
					f.set(model, v);
				} else if (v instanceof Long || v instanceof AtomicLong) {
					f.set(model, v);
				} else if (v instanceof Double) {
					f.setDouble(model, (Double) v);
				} else if (v instanceof Boolean) {
					f.setBoolean(model, (Boolean) v);
				} else {
					f.set(model, v);
				}

			}

			return model;
		}
		return null;
	}

	/**
	 * 
	 * @param model
	 *            {@link IModel}
	 * @return {@link Criteria}
	 * @throws Exception
	 */
	public static Criteria getCriteriaEqual(IModel model) throws Exception {
		Criteria criteria = new Criteria();
		Field[] fieldsTab = model.getClass().getDeclaredFields();
		List<Field> fields = Arrays.asList(fieldsTab);
		int index = 0;
		for (Field f : fields) {
			Object v = f.get(model);
			if (v != null && index > 0) {
				criteria.andOperator(Criteria.where(f.getName()).is(v));
			}
			if (v != null && index == 0) {
				criteria.where(f.getName()).is(v);
			}
			index++;
		}
		return criteria;
	}
}
