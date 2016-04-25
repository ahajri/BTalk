package com.ahajri.btalk.data.repository;

import java.util.List;

import com.marklogic.client.io.DocumentMetadataHandle;

/**
 * Show case for a simple repository allowing to access and modify
 * {@link IModel} objects in a domain specific way.
 *
 * @author <p>
 *         Anis HAJRI
 *         </p>
 */
public interface IRepository<T> {

	void persist(T model) throws Exception;

	boolean remove(T model) throws Exception;

	T findOne(Object... params) throws Exception;

	List<T> findAll() throws Exception;

	List<T> findByQuery(String query) throws Exception;

	List<T> searchByExample(String example);

	Long count() throws Exception;

	void update(T model) throws Exception;

	void replaceInsert(T model, String fragment) throws Exception;

	List<T> findById(String id);

	void persist(T model, DocumentMetadataHandle metadata);

}
