package com.ahajri.btalk.data.repository;

import java.util.List;

import com.ahajri.btalk.data.domain.AModel;

/**
 * Showcase for a simple repository allowing to access and modify {@link AModel}
 * objects in a domain specific way.
 *
 * @author Anis HAJRI
 */
public interface IRepository<T> {

	void add(T model) throws Exception;

	void remove(T model) throws Exception;

	T findOne(Object... params) throws Exception;

	List<T> findAll() throws Exception;

	List<T> findByQuery(String query) throws Exception;

	Long count() throws Exception;

	void update(T model) throws Exception;

	void replaceInsert(T model, String fragment) throws Exception;

}
