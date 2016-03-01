package com.ahajri.btalk.data.service;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ahajri.btalk.data.domain.UserAuth;
import com.ahajri.btalk.utils.MongoUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

@Service("userService")
public class UserAuthService extends AMongoService<UserAuth> {

	/** Logger */
	private final Logger LOGGER = Logger.getLogger(getClass());

	@Autowired
	private MongoClient mongo;

	@Override
	public UserAuth persist(UserAuth model) {
		try {
			mongoTemplate.save(model, "UserAuth");
		} catch (Exception e) {
			LOGGER.error(e);
			return null;
		}
		LOGGER.info("UserAuth added with id: " + model.get_id());
		return model;
	}

	@Override
	public Integer remove(UserAuth model) throws Exception {
		Integer count = Integer.parseInt("0");
		try {
		    DB db = mongo.getDB(databaseName);
		    DBCollection col = db.getCollection(UserAuth.class
			    .getSimpleName());
		    BasicDBObject query = MongoUtils.model2DbObject(model);
		    WriteResult wResult = col.remove(query);
		    count = wResult.getN();
		    LOGGER.info(count + " UserAuth Documents removed");
		} catch (Exception e) {
		    LOGGER.error(e);
		    return -1;
		}
		return count;
	}

	@Override
	public Integer modifyAll(UserAuth query, UserAuth update) throws Exception {
		return null;
	}

	@Override
	public Integer modify(UserAuth query, UserAuth update, boolean upsert,
			boolean multi) throws Exception {
		return null;
	}

	@Override
	public List<UserAuth> findAll() {
		return mongoTemplate.findAll(UserAuth.class,"UserAuth");
	}

}
