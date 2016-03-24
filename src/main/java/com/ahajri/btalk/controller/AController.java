package com.ahajri.btalk.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.ahajri.btalk.data.domain.AModel;
import com.ahajri.btalk.data.domain.Discussion;
import com.ahajri.btalk.data.domain.upsert.DiscussionUpsert;
import com.ahajri.btalk.data.domain.upsert.DiscussionsUpsert;
import com.ahajri.btalk.error.ClientErrorInformation;
import com.marklogic.client.ResourceNotFoundException;

/**
 * 
 * @author <p>
 *         ahajri
 *         </p>
 */
public abstract class AController<T extends AModel> {


	protected final int GT = 1;
	protected final int EQ = 2;
	protected final int LT = 3;

	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Resource not found")
	@ExceptionHandler(ResourceNotFoundException.class)
	public void handleResourceNotFoundException() {
	}

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Error")
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientErrorInformation> handleException(HttpServletRequest req, Exception ex) {
		ex.printStackTrace();
		ClientErrorInformation e=new ClientErrorInformation(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return new ResponseEntity<ClientErrorInformation>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	/**
	 * find models by query
	 * 
	 * @param query
	 *            : query {@link AModel} Object
	 * @return: {@link ResponseEntity}
	 */
	public abstract ResponseEntity<List<T>> findByQuery(@RequestBody String query);

	/**
	 * find nuique model
	 * 
	 * @param query
	 *            : query {@link AModel} Object
	 * @return {@link ResponseEntity}
	 */
	public abstract ResponseEntity<T> findOne(@RequestBody T query);

	/**
	 * remove {@link Model}
	 * 
	 * @param query
	 *            : query {@link AModel} Object
	 * @return {@link Boolean}
	 */
	public abstract Integer delete(T query);

	/**
	 * insert document ni collection
	 * @param model
	 *            : Document instance to insert
	 * @return inserted document
	 */
	public abstract ResponseEntity<T> create(T model);
	
	
	/**
	 * 
	 * @return {@link List}of all found documents
	 */
	
	public abstract ResponseEntity<List<T>> findAll();
	
	/**
	 * 
	 * @return
	 */
//	public abstract ResponseEntity<T> update(DiscussUpsert modelUpsert);
//
//	public abstract ResponseEntity<T> update(DiscussionsUpsert modelUpsert) ;

}
