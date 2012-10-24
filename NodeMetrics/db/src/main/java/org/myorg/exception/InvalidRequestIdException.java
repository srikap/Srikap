package org.myorg.exception;

/**
 * 
 * @author srikap
 *
 */
public class InvalidRequestIdException extends NMException {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Invalid request. Request does not exist!
	 * @param message
	 */
	public InvalidRequestIdException(String message) {
		super(message);
	}
}
