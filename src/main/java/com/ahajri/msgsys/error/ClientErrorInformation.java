package com.ahajri.msgsys.error;

/**
 * 
 * @author <p>
 *         ahajri
 *         </p>
 *
 */
public class ClientErrorInformation {

	private String errorMsg;
	private String httpStatusURI;

	public ClientErrorInformation(String errorMsg, String httpStatus) {
		this.errorMsg = errorMsg;
		this.httpStatusURI = httpStatus;
	}

	/**
	 * @return the errorMsg
	 */
	public String getErrorMsg() {
		return errorMsg;
	}

	/**
	 * @param errorMsg
	 *            the errorMsg to set
	 */
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**
	 * @return the httpStatusURI
	 */
	public String getHttpStatusURI() {
		return httpStatusURI;
	}

	/**
	 * @param httpStatusURI
	 *            the httpStatusURI to set
	 */
	public void setHttpStatusURI(String httpStatusURI) {
		this.httpStatusURI = httpStatusURI;
	}

}
