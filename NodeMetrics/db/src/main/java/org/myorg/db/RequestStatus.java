package org.myorg.db;

/**
 * Possible request states.
 * @author srikap
 *
 */
public enum RequestStatus {
	NEW("New"),
	COMPLETED("Completed"),
	FAILED("Failed"), 
	IN_PROGRESS("InProgress");

	private String status;

	@Override
	public String toString(){
		return status;
	}

	private RequestStatus(String status){
		this.status = status;
	}
}
