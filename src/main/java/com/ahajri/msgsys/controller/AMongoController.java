package com.ahajri.msgsys.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;

import com.ahajri.msgsys.domain.IModel;
import com.ahajri.msgsys.error.ClientErrorInformation;

/**
 * 
 * @author <p>
 *         ahajri
 *         </p>
 */
public abstract class AMongoController<T> {

	private final Logger LOGGER = Logger.getLogger(getClass());

	protected final int GT = 1;
	protected final int EQ = 2;
	protected final int LT = 3;

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ClientErrorInformation> handleException(
			HttpServletRequest req, Exception e) {
		LOGGER.error("Rules For Rest Not Found >>>>> Problem while executing request: "
				+ e.getMessage());

		ClientErrorInformation error = new ClientErrorInformation(e.toString(),
				req.getRequestURI());
		return new ResponseEntity<ClientErrorInformation>(error,
				HttpStatus.NOT_FOUND);
	}

	/**
	 * find models by query
	 * 
	 * @param query
	 *            : query {@link IModel} Object
	 * @return: {@link ResponseEntity}
	 */
	public abstract ResponseEntity<List<T>> findByQuery(@RequestBody T query);

	/**
	 * find nuique model
	 * 
	 * @param query
	 *            : query {@link IModel} Object
	 * @return {@link ResponseEntity}
	 */
	public abstract ResponseEntity<T> findOne(@RequestBody T query);

	/**
	 * remove {@link Model}
	 * 
	 * @param query
	 *            : query {@link IModel} Object
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

}
