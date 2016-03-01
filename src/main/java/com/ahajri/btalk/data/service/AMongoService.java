package com.ahajri.btalk.data.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.ahajri.btalk.data.domain.IModel;
import com.ahajri.btalk.data.repository.SequenceMongoRepository;
import com.mongodb.DBObject;

/**
 * 
 * @author ahajri
 */
public abstract class AMongoService<T extends IModel> {

	protected static final String gt = "$gt";

	@Autowired
	protected String databaseName;

	@Autowired
	protected MongoTemplate mongoTemplate;

	@Autowired
	protected SequenceMongoRepository sequenceRepository;

	/**
	 * Persist document
	 * 
	 * @param model
	 *            : {@link IModel} to persist
	 */

	public abstract T persist(T model);

	/**
	 * remove document
	 * 
	 * @param model
	 * @throws Exception
	 */
	public abstract Integer remove(T model) throws Exception;

	/**
	 * modify all found documents
	 * 
	 * @param query
	 * @param update
	 * @return count of modified documents
	 * 
	 * @throws Exception
	 */
	public abstract Integer modifyAll(T query, T update) throws Exception;

	/**
	 * 
	 * @param query
	 * @param update
	 *            : {@link DBObject} to update
	 * @param upsert
	 *            : if true create when not found
	 * @param multi
	 *            : all found or only first
	 * @return
	 * 
	 * @throws Exception
	 */
	public abstract Integer modify(T query, T update, boolean upsert,
			boolean multi) throws Exception;

	/**
	 * 
	 * @return {@link List} of all Documents
	 */
	public abstract List<T> findAll();

}
