package org.myorg.monitor.rest;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.myorg.db.MetricsDAO;
import org.myorg.db.PerformanceData;
import org.myorg.db.PerformanceDataFields;
import org.myorg.exception.NMException;

import com.google.inject.Inject;


@Path("/nodeMetrics")
public class NodeMetrics {
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	private Logger logger = Logger.getLogger(NodeMetrics.class);
	@Inject MetricsDAO metricsDAO = null;

//	/**
//	 * creates a request to get node metrics.
//	 * @param nodeNames
//	 * @return requestId as String
//	 */
//	@SuppressWarnings("unchecked")
//	@POST
//	@Produces(MediaType.APPLICATION_JSON)
//	@Consumes(MediaType.APPLICATION_JSON)
//	public Response getNodeMetrics(String[] nodeNames){
//		logger.debug("getNodeMetrics. nodeNames:" + nodeNames);
//		String requestId = submit(Arrays.asList(nodeNames));
//		JSONObject response = new JSONObject();
//		response.put("requestId", requestId);
//		logger.debug("created request with requestId:" + requestId);
//		return Response.ok(response.toJSONString()).build();
//
//	}


	/**
	 * creates a request to get node metrics.
	 * @param nodeNames
	 * @return requestId as String
	 */
	@SuppressWarnings("unchecked")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{nodeNames}")
	public Response getNodeMetrics(@PathParam("nodeNames") String nodeNames){
		logger.debug("getNodeMetrics. nodeNames:" + nodeNames);
		if (nodeNames == null || nodeNames.length() == 0){
			logger.error("Received a bad request");
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		
		//Create a request in DB
		String[] nodeNamesAsArray = nodeNames.split(",");
		String requestId = generateRequestId();
		String requestTime = String.valueOf(System.currentTimeMillis());
		metricsDAO.createRequest(requestId, Arrays.asList(nodeNamesAsArray), requestTime);
		
		//Create JSON response
		JSONObject response = new JSONObject();
		response.put("requestId", requestId);
		logger.debug("created request with requestId:" + requestId);
		return Response.ok(response.toJSONString()).build();
	}


	/**
	 * Query the status of the request.
	 * Depending on the persistence policy, older requests may be purged by the system.
	 * @param requestId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/request/{requestId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRequestStatus(@PathParam("requestId") String requestId){
		JSONObject response = new JSONObject();
		response.put(PerformanceDataFields.REQUEST_ID, requestId);
		JSONArray responseList = new JSONArray();
		logger.trace("Find status of request:"+requestId);
		try {
			Iterator<PerformanceData> performanceDataList = getStatus(requestId);
			logger.trace("Received response from Node Monitoring service for the request");

			while(performanceDataList.hasNext()){
				PerformanceData performanceData = performanceDataList.next();
				JSONObject jsonPerfData =  getJSON(performanceData);
				responseList.add(jsonPerfData);
			}
			response.put("Result", responseList);
		} catch (NMException ex){
			logger.error("Exception:" + ex.getMessage());
			throw new WebApplicationException(Response.Status.BAD_REQUEST);
		}
		logger.debug(" status of request:"+ requestId + ":" + responseList.toJSONString());
		return Response.ok(response.toJSONString()).build();
	}

	/**
	 * Constructs JSONString representation for the performanceData
	 * @param performanceData
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getJSON(PerformanceData performanceData) {
		JSONObject object = new JSONObject();
		object.put(PerformanceDataFields.COLLECTION_TIME, performanceData.getCollectionTime());
		object.put(PerformanceDataFields.NODE_NAME, performanceData.getNodeName());
		object.put(PerformanceDataFields.PERFORMANCE_DATA, performanceData.getResponse());
		object.put(PerformanceDataFields.REQUEST_STATUS, performanceData.getRequestStatus());
		return object;
	}
	
	/**
	 * requestId generation
	 * @return
	 */
	private String generateRequestId() {
		return UUID.randomUUID().toString();
	}
	/* (non-Javadoc)
	 * @see org.myorg.monitor.NodeMonitorService#getRequestStatus(java.lang.String)
	 */
	public Iterator<PerformanceData> getStatus(String requestId) throws NMException {
		logger.trace("find status for request:"+requestId);
		return metricsDAO.getRequestStatus(requestId);
	}


}
