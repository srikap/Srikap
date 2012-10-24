package org.myorg.monitor.impl;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.myorg.db.MetricsDAO;
import org.myorg.db.RequestStatus;
import org.myorg.monitor.NodeCommand;

import com.google.inject.Inject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 * Optimizes the network calls by grouping multiple requests to a node into one.
 * @author srikap
 *
 */
@SuppressWarnings("unchecked")
public class NodeCommandImpl implements NodeCommand, Runnable{
	//number of retries to connect to the remote node.
	private static final int retryCount = 3;
	private Logger logger = Logger.getLogger(NodeCommandImpl.class);
	private final static String REQUEST_TYPE = "metrics";
	private final static String STATUS = "Status";
	
	//Failure status message
	@SuppressWarnings("unchecked")
	protected static final String FAILURE_MESSAGE;
	static {
		JSONObject failureMsg = new JSONObject();
		failureMsg.put(STATUS, "Not Reachable");
		FAILURE_MESSAGE = failureMsg.toJSONString();
	}
	
	//nodeName
	private String nodeName = null;
	@Inject	protected MetricsDAO metricsDAO = null;
	
	/* (non-Javadoc)
	 * @see org.myorg.monitor.impl.NodeCommand#run()
	 */
	public void run() {
		Client client = Client.create();
		//connect to remote node
		WebResource webResource = client.resource("http://" + getNodeName() + REQUEST_TYPE);
		int numberOfConnectAttempts = 0;

		try {
			// Try upto numberOfConnectAttempts.
			while (numberOfConnectAttempts  < retryCount) {
				ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
				//Success
				if (response.getStatus() == 200){
					setStatus(response.getEntity(String.class), RequestStatus.COMPLETED);
					return;
				}
				//Failure
				logger.debug("Failed to connect to the node:" 
						+ getNodeName() 
						+ " Status:" 
						+ response.getStatus());
				
				numberOfConnectAttempts++;
				if (numberOfConnectAttempts < retryCount){
					//Retry
					logger.debug("Retrying ...");
				} else {
					//Failed to Connect
					setStatus(getJsonString(STATUS, "Could not connect after " + numberOfConnectAttempts + " attempts"), RequestStatus.FAILED);
					return;
				} 
			}
		} catch (Exception ex){
			logger.error(ex.getMessage());
			setStatus(getJsonString(STATUS, ex.getMessage()), RequestStatus.FAILED);
			return;
		}
		setStatus( FAILURE_MESSAGE, RequestStatus.FAILED);
	}
	
	/**
	 * creates a json string rep.
	 * @param key
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unused")
	private static String getJsonString(String key, String value){
		JSONObject msgObject = new JSONObject();
		msgObject.put(key, value);
		return msgObject.toJSONString();
	}

	/**
	 * Update database with the status and response from the remote node
	 * @param status
	 * @param requestStatus
	 */
	public void setStatus(String status, RequestStatus requestStatus) {
		String now = String.valueOf(System.currentTimeMillis());
		if (metricsDAO == null){
			logger.fatal("metricsDAO is null!");
		}
		metricsDAO.storeMetrics(nodeName, now, status, requestStatus);
	}



	public MetricsDAO getMetricsDAO() {
		return metricsDAO;
	}


	public void setMetricsDAO(MetricsDAO metricsDAO) {
		this.metricsDAO = metricsDAO;
	}


	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}


}
