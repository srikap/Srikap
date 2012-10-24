package org.myorg.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.myorg.db.PerformanceDataFields.NODE_NAME;
import static org.myorg.db.PerformanceDataFields.REQUEST_ID;
import static org.myorg.db.PerformanceDataFields.REQUEST_STATUS;

import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.junit.Test;
import org.myorg.exception.InvalidRequestIdException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DBTest extends MongoStore{


	/**
	 * Tests wether the requests are created as expected.
	 * A request is decomposed into an individual request for each node, and all have same requestId.
	 * Composite.
	 */
	@Test
	public void createRequestTest(){
		long countBeforeCreate = performanceData.getCount();
		String requestId="request1";
		String node1 = UUID.randomUUID().toString();
		String node2 =  UUID.randomUUID().toString();
		String[] nodeNames = {node1, node2};
		String requestTime = String.valueOf(System.currentTimeMillis());
		super.createRequest(requestId, Arrays.asList(nodeNames), requestTime);
		long countAfterCreate = performanceData.getCount();
		assertEquals(countBeforeCreate + 2, countAfterCreate);
		Iterator<PerformanceData> perfDataList = null;
		try {
			perfDataList = getRequestStatus(requestId);
			while (perfDataList.hasNext()){
				PerformanceData perfData = perfDataList.next();
				if (perfData.getNodeName().equals(node1) ||
						perfData.getNodeName().equals(node2)){
					assertEquals( RequestStatus.NEW.toString(), perfData.getRequestStatus());
				}
			}
		} catch (InvalidRequestIdException e) {
			fail("Request with requestId:" + requestId + " should not throw exception");
		}

	}
	

	/**
	 * Check whether the request moves from NEW to IN_PROGRESS state
	 */
	@Test
	public void getNodeNameRequestedTest(){
		createRequestTest();
		String nodeName = super.getNodenameRequested();
		assertNotNull(nodeName);
		DBObject queryDoc = new BasicDBObject();
		queryDoc.put(NODE_NAME, nodeName);
		DBCursor cursor = performanceData.find(queryDoc);
		while (cursor.hasNext()){
			DBObject object = cursor.next();
			String status = (String)object.get(REQUEST_STATUS);
			assertFalse(status.equals(RequestStatus.NEW.toString()));
		}
	}

	/**
	 * Check whether the request is stored correctly, updating the status rightly.
	 */
	@Test
	public void storeMetricsTest(){
		getNodeNameRequestedTest();
		DBObject queryDoc = new BasicDBObject();
		queryDoc.put(REQUEST_STATUS, RequestStatus.IN_PROGRESS.toString());
		DBCursor cursor = performanceData.find(queryDoc);
		String nodeName = null;
		String requestId = null;
		while (cursor.hasNext()){
			DBObject object = cursor.next();
			nodeName = (String) object.get(NODE_NAME);
			requestId = (String) object.get(REQUEST_ID);
			logger.debug("requestId:" + requestId);
			logger.debug("nodeName:" + nodeName);
			String timestamp = String.valueOf(System.currentTimeMillis());
			super.storeMetrics(nodeName, timestamp, "Fine", RequestStatus.COMPLETED);
		}
		Iterator<PerformanceData> perfDataList;
		try {
			perfDataList = getRequestStatus(requestId);
			while (perfDataList.hasNext()){
				PerformanceData perfData = perfDataList.next();
				if (perfData.getNodeName().equals(nodeName)){
					assertEquals( RequestStatus.COMPLETED.toString(), perfData.getRequestStatus());
				}
			}
		} catch (InvalidRequestIdException e) {
			fail("Request with requestId:" + requestId + " should not throw exception");
		}


	}
}