package org.myorg.db;

import static org.myorg.db.PerformanceDataFields.COLLECTION_TIME;
import static org.myorg.db.PerformanceDataFields.NODE_NAME;
import static org.myorg.db.PerformanceDataFields.PERFORMANCE_DATA;
import static org.myorg.db.PerformanceDataFields.REQUEST_ID;
import static org.myorg.db.PerformanceDataFields.REQUEST_STATUS;
import static org.myorg.db.PerformanceDataFields.REQUEST_TIME;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.myorg.exception.InvalidRequestIdException;
import org.myorg.main.NodeMonitorServiceConfigurator;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;
import com.mongodb.WriteResult;

public class MongoStore implements MetricsDAO{
	//database instance name
	private static final String DATABASE_NAME;
	//database username
	private static final String DB_USER_NAME;
	//database password
	private static final String DB_USER_PASSWD;
	//table containing node monitoring metrics
	private static final String PERFORMANCE_DATA_COLLECTION = "performance_data";
	//In case of replica sets provide MONGO URL. If a single db node is used, leave it as null.
	private static final String DB_URI;
	//mongodb port
	private static final Integer PORT;
	//null in case of replica sets, otherwise provide the host name.
	private static final String DATABASE_HOSTNAME;
	//default database name to use
	private static final String DEFAULT_DB_NAME = "nodeMonitorDB";
	//default database host
	private static final String DEFAULT_DB_HOSTNAME = "localhost";
	//default mongodb port
	private static final int DEFAULT_DB_PORT = 27017;
	//default mongodb username
	private static final String DEFAULT_DB_USERNAME = "nodeManager";
	//default mongodb password. Read it from properties, no default;
	private static final String DEFAULT_DB_PASSWD = null;
	//default DB uri, mongo URI.
	private static final String DEFAULT_DB_URI = null;
	//property key for database name
	private static final String DEFAULT_DB_NAME_PROPERTY = "org.myorg.monitor.db.name";
	//property key for database hostname
	private static final String DEFAULT_DB_HOSTNAME_PROPERTY = "org.myorg.monitor.db.hostname";
	//property key for port
	private static final String DEFAULT_DB_PORT_PROPERTY = "org.myorg.monitor.db.port";
	//property key for database username
	private static final String DEFAULT_DB_USERNAME_PROPERTY = "org.myorg.monitor.db.username";
	//property key for database password
	private static final String DEFAULT_DB_PASSWD_PROPERTY = "org.myorg.monitor.db.passwd";
	//property key for mongo db uri
	private static final String DEFAULT_DB_URI_PROPERTY = "org.myorg.monitor.db.uri";
	//Mongo db
	private DB db = null;

	//Initialize properties
	static {
		DATABASE_NAME = NodeMonitorServiceConfigurator.getInstance().getProperty(DEFAULT_DB_NAME_PROPERTY, DEFAULT_DB_NAME);
		DATABASE_HOSTNAME = NodeMonitorServiceConfigurator.getInstance().getProperty(DEFAULT_DB_HOSTNAME_PROPERTY, DEFAULT_DB_HOSTNAME);
		PORT = NodeMonitorServiceConfigurator.getInstance().getIntProperty(DEFAULT_DB_PORT_PROPERTY, DEFAULT_DB_PORT);
		DB_USER_NAME = NodeMonitorServiceConfigurator.getInstance().getProperty(DEFAULT_DB_USERNAME_PROPERTY, DEFAULT_DB_USERNAME);
		DB_USER_PASSWD = NodeMonitorServiceConfigurator.getInstance().getProperty(DEFAULT_DB_PASSWD_PROPERTY, DEFAULT_DB_PASSWD);
		DB_URI = NodeMonitorServiceConfigurator.getInstance().getProperty(DEFAULT_DB_URI_PROPERTY, DEFAULT_DB_URI);
	}

	//Collection name for NodeMonitor's metrics
	protected DBCollection performanceData;

	protected Logger logger = Logger.getLogger(MongoStore.class);

	//Hide the constructor
	public MongoStore() {

		try {
			Mongo m = null;
			//For replica sets
			if (DB_URI == null || DB_URI.length() == 0){
				m = new Mongo(DATABASE_HOSTNAME, PORT);
			} else {
				//For single node
				m = new Mongo(new MongoURI(DB_URI));
			}
			db = m.getDB(DATABASE_NAME);
			//TODO: Removing authentication, for simplicity...
			//db.authenticate(DB_USER_NAME, DB_USER_PASSWD.toCharArray());
			performanceData = db.getCollection(PERFORMANCE_DATA_COLLECTION);
			if (performanceData == null) {
				//create the collection if it does not exist.
				performanceData = db.createCollection(PERFORMANCE_DATA_COLLECTION, new BasicDBObject());
			}
			performanceData.ensureIndex("request_id");

		} catch (UnknownHostException ex){
			logger.fatal("DBHostname:" + DATABASE_HOSTNAME + " is not reachable.");
		}
	}

	/**
	 * Called to update database with the Node Metrics fetched from remote node
	 * @param nodeName: can be either IP address or FQDN
	 * @param requestId: Unique requestID
	 * @param timestamp: in milli-seconds.
	 * @param jsonData: response from the remote node
	 */
	public void storeMetrics(String nodeName, String timestamp, String jsonData, RequestStatus requestStatus){
		logger.trace("nodeName:" + nodeName +  
				" timestamp:" + timestamp + " jsonData:" + jsonData);
		BasicDBObject queryDoc = new BasicDBObject();
		queryDoc.put(NODE_NAME, nodeName);
		List<String> requestStates = new ArrayList<String>(2);
		requestStates.add(RequestStatus.IN_PROGRESS.toString());
		requestStates.add(RequestStatus.NEW.toString());
		queryDoc.put(REQUEST_STATUS, new BasicDBObject("$in", requestStates));
		Map<String, String> map = new HashMap<String, String>();
		map.put(COLLECTION_TIME, timestamp);
		map.put(PERFORMANCE_DATA, jsonData);
		map.put(REQUEST_STATUS, requestStatus.toString());
		BasicDBObject updateDoc = new BasicDBObject().append("$set", new BasicDBObject(map));
		performanceData.updateMulti(queryDoc, updateDoc);
	}

	/**
	 * called after a new request is received from the clients of Node Monitoring Service
	 * @param nodeNames: List of nodenames in the request
	 * @param requestTime: Requested time
	 */
	public void createRequest(String requestId, List<String> nodeNames, String requestTime){
		if (nodeNames == null){
			logger.debug("nodeNames param is null");
			return;
		}
		List<DBObject> docs = new ArrayList<DBObject>(nodeNames.size());
		for (String nodeName : nodeNames) {
			DBObject doc = new BasicDBObject();
			doc.put(NODE_NAME, nodeName);
			doc.put(REQUEST_ID, requestId);
			doc.put(REQUEST_TIME, requestTime);
			doc.put(REQUEST_STATUS, RequestStatus.NEW.toString());
			docs.add(doc);
		}
		logger.debug("Inserting into db");
		WriteResult result = performanceData.insert(docs);
		CommandResult commandResult = result.getLastError();
		if (commandResult.getErrorMessage() != null){
			logger.error("Failed to insert performance data");
		} else {
			logger.trace("Inserted into " + PERFORMANCE_DATA_COLLECTION);
		}
	}

	/**
	 * 
	 * @param requestId: search for the request with the requestId
	 * @return: Iterator for performance Data.
	 * @throws InvalidRequestIdException: Exceptions such as non-existing requestId.
	 */
	public Iterator<PerformanceData> getRequestStatus(String requestId) throws InvalidRequestIdException{
		logger.trace("finding request with requestId:" + requestId);
		DBObject queryDoc = new BasicDBObject();
		queryDoc.put(REQUEST_ID, requestId);
		DBCursor cursor = performanceData.find(queryDoc);
		if (cursor == null || cursor.size() == 0) {
			throw new InvalidRequestIdException("There are no requests with the requestId:" + requestId);
		}
		return new PerformanceDataIterator(cursor);

	}

	/**
	 * Gets the next node in the request queue.
	 * And update the requestStatus to IN_PROGRESS for all requests which requested this node
	 */
	public String getNodenameRequested() {
		DBObject queryDoc = new BasicDBObject();
		queryDoc.put(REQUEST_STATUS, RequestStatus.NEW.toString());
		DBCursor cursor = performanceData.find(queryDoc).sort( new BasicDBObject(REQUEST_TIME, -1)).limit(1);

		if (cursor.hasNext()){
			DBObject object = cursor.next();
			String nodeName =  (String) object.get(NODE_NAME);
			DBObject updateDoc = new BasicDBObject().append("$set", 
					new BasicDBObject()
			.append(REQUEST_STATUS, RequestStatus.IN_PROGRESS.toString())
			.append(NODE_NAME, nodeName));
			queryDoc.put(NODE_NAME, nodeName);
			WriteResult result = performanceData.updateMulti(queryDoc, updateDoc);
			CommandResult commandResult = result.getLastError();
			if (commandResult.getErrorMessage() != null){
				logger.error("Failed to update performance data");
			} else {
				logger.trace("Updated ... " + PERFORMANCE_DATA_COLLECTION); // TODO:update the comments
			}
			return nodeName;
		}
		return null;
	}


	/**
	 * Iterator for node metrics.
	 * @author srikap
	 *
	 */
	private class PerformanceDataIterator implements Iterator<PerformanceData> {
		//DBCursor returned by Mongo
		private DBCursor cursor;

		public PerformanceDataIterator(DBCursor dbCursor){
			this.cursor = dbCursor;
		}

		public boolean hasNext() {

			return cursor.hasNext();
		}

		public PerformanceData next() {
			BasicDBObject object = (BasicDBObject) cursor.next();

			return createPerformanceData(object);
		}

		/**
		 * construct PeformanceData for a single node using record/doc fetched from db.
		 * @param object
		 * @return
		 */
		private PerformanceData createPerformanceData(BasicDBObject object) {
			String requestId = (String)object.get(REQUEST_ID);
			String collectionTime = (String)object.get(COLLECTION_TIME);
			String nodeName = (String)object.get(NODE_NAME);
			String requestTime = (String)object.get(REQUEST_TIME);
			String response = (String)object.get(PERFORMANCE_DATA);
			String requestStatus = (String)object.get(REQUEST_STATUS);

			return new PerformanceData.Builder()
			.requestId(requestId)
			.nodeName(nodeName)
			.collectionTime(collectionTime)
			.requestTime(requestTime)
			.response(response)
			.requestStatus(requestStatus)
			.build();
		}

		public void remove() {
			throw new UnsupportedOperationException();

		}

	}

}
