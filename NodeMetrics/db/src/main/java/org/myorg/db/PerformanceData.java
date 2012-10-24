package org.myorg.db;



/**
 * PerformanceData is a representation of node metrics data, responded by one single node.
 * request status indicates the state of the request: new/completed/failed
 * @author srikap
 *
 */
public class PerformanceData {
	//ip-address or fqdn
	private String nodeName;
	//requestId
	private String requestId;
	//requested at time
	private String requestTime;
	//collected from remote node at time
	private String collectionTime;
	//response from remote node or error message as JSON string
	private String response;
	//request status of new/completed/failed
	private String requestStatus;


	public String getRequestStatus() {
		return requestStatus;
	}

	public void setStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getCollectionTime() {
		return collectionTime;
	}

	public void setCollectionTime(String collectionTime) {
		this.collectionTime = collectionTime;
	}

	//Builder to encapsulate construction of PerformanceData.
	public static class Builder {
		PerformanceData perfData = null;
		public Builder(){
			perfData = new PerformanceData();
		}
		public Builder nodeName(String nodeName){
			perfData.nodeName = nodeName;
			return this;
		}

		public Builder requestId(String requestId){
			perfData.requestId = requestId;
			return this;
		}

		public Builder requestTime(String requestTime){
			perfData.requestTime = requestTime;
			return this;
		}

		public Builder collectionTime(String collectionTime){
			perfData.collectionTime = collectionTime;
			return this;
		}
		public Builder requestStatus(String requestStatus){
			perfData.requestStatus = requestStatus;
			return this;
		}
		public Builder response(String response){
			perfData.response = response;
			return this;
		}
		public PerformanceData build(){
			return perfData;
		}
	}

}
