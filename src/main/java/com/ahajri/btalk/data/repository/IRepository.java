package com.ahajri.btalk.data.repository;

import java.util.List;

import com.ahajri.btalk.data.domain.IModel;

/**
 * Showcase for a simple repository allowing to access and modify {@link IModel}
 * objects in a domain specific way.
 *
 * @author Anis HAJRI
 */
public interface IRepository<T> {

	void add(T model);

	void remove(T model);

	T findOne(Object... params);

	List<T> findAll();

	List<T> findByQuery(String query);

	Long count();
	
	void update(T model);

}
