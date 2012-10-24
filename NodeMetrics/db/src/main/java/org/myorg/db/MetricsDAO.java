package org.myorg.db;

import java.util.Iterator;
import java.util.List;

import org.myorg.exception.NMException;

/**
 * Interface for persistence for Node Monitoring Application
 * @author srikap
 *
 */
public interface MetricsDAO {
	/**
	 * Called to update database with the Node Metrics fetched from remote node
	 * @param requestStatus 
	 * @param nodeName: can be either IP address or FQDN
	 * @param requestId: Unique requestID
	 * @param timestamp: in milli-seconds.
	 * @param jsonData: response from the remote node
	 */
	void storeMetrics(String nodeName, String timestamp, String jsonData, RequestStatus requestStatus);

	/**
	 * called after a new request is received from the clients of Node Monitoring Service
	 * 
	 * @param requestId: create a request with the new requestId
	 * @param nodeNames: List of nodenames in the request
	 * @param requestTime: Requested time
	 */
	void createRequest(String requestId, List<String> nodeNames, String requestTime);

	/**
	 * 
	 * @param requestId: search for the request with the requestId
	 * @return: Iterator for performance Data.
	 * @throws NMException: Exceptions such as non-existing requestId.
	 */
	Iterator<PerformanceData> getRequestStatus(String requestId) throws NMException;

	String getNodenameRequested();
}
