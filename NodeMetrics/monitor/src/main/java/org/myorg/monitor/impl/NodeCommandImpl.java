package org.myorg.monitor.impl;

import org.apache.log4j.Logger;
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
public class NodeCommandImpl implements NodeCommand, Runnable{
	//number of retries to connect to the remote node.
	private static final int retryCount = 3;
	private Logger logger = Logger.getLogger(NodeCommandImpl.class);
	//Failure status message
	protected static final String FAILURE_MESSAGE = "{\"status\":\"Not reachable\"}";
	//nodeName
	private String nodeName = null;
	@Inject	protected MetricsDAO metricsDAO = null;
	/* (non-Javadoc)
	 * @see org.myorg.monitor.impl.NodeCommand#run()
	 */
	public void run() {
		Client client = Client.create();
		WebResource webResource = client.resource("http://" + getNodeName() + "/metrics");
		int numberOfConnectAttempts = 0;

		try {
			while (numberOfConnectAttempts  < retryCount) {
				ClientResponse response = webResource.accept("application/json").get(ClientResponse.class);
				//SUCCESS
				if (response.getStatus() == 200){
					setStatus(response.getEntity(String.class), RequestStatus.COMPLETED);
					return;

				}
				logger.debug("Failed to connect to the node:" 
						+ getNodeName() 
						+ " Status:" 
						+ response.getStatus());
				numberOfConnectAttempts++;
				if (numberOfConnectAttempts < retryCount){
					logger.debug("Retrying");
				} else {
					//Failed to Connect
					setStatus("{ \"status\" : \""  + response.getStatus() + "\" }", RequestStatus.FAILED);
					return;
				} 
			}
		} catch (Exception ex){
			logger.error(ex.getMessage());
			setStatus("{ \"status\" : \""  + ex.getMessage() + "\" }", RequestStatus.FAILED);
			return;
		}
		setStatus( FAILURE_MESSAGE, RequestStatus.FAILED);
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
