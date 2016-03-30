package com.ahajri.btalk.data.service;

import java.util.List;

import com.ahajri.btalk.data.domain.AModel;
import com.mongodb.DBObject;

/**
 * 
 * @author ahajri
 */
public abstract class AService<T extends AModel> {

	public static final String DISCUSSION_COLLECTION = "/DiscussionCollection";

	/**
	 * Persist document
	 * 
	 * @param model
	 *            : {@link AModel} to persist
	 */

	public abstract T create(T model);

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

	/**
	 * Search Models
	 * 
	 * @param q
	 * @return {@link List} of found Models
	 */
	public abstract List<T> search(String q);

	public abstract List<T> findByQuery(String q);

	public abstract void replaceInsert(T model, String fragment);

}
