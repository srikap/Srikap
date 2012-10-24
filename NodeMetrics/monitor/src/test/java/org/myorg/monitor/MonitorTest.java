package org.myorg.monitor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.myorg.db.MetricsDAO;
import org.myorg.db.MongoStore;
import org.myorg.db.PerformanceData;
import org.myorg.db.RequestStatus;
import org.myorg.exception.NMException;
import org.myorg.monitor.impl.NodeMonitorServiceImpl;

@RunWith(JUnit4.class)
public class MonitorTest extends MongoStore{

	static NodeMonitorService nms = null;
	static MetricsDAO metricsDAO = null;

	@BeforeClass
	public static void initialize(){
		nms = MockConfigModule.getInjector().getInstance(NodeMonitorServiceImpl.class);
		metricsDAO = MockConfigModule.getInjector().getInstance(MetricsDAO.class);
	}


	@Test
	public void service(){
		String requestId = UUID.randomUUID().toString();
		String nodeName = UUID.randomUUID().toString();
		Thread nmsThread = new Thread(nms);
		nmsThread.start();
		String requestTime = String.valueOf(System.currentTimeMillis());
		metricsDAO.createRequest(requestId, Arrays.asList(new String[] {nodeName}), requestTime);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException  e) {
			logger.trace(e.getMessage());
		}
		Iterator<PerformanceData> perfDataList = null;
		try {
			perfDataList = metricsDAO.getRequestStatus(requestId);
			if(perfDataList.hasNext()){
				PerformanceData perfData = perfDataList.next();
				assertEquals(RequestStatus.COMPLETED.toString(), perfData.getRequestStatus());
				assertEquals(requestTime, perfData.getRequestTime());
				assertEquals(requestId, perfData.getRequestId());
				assertNotNull(perfData.getResponse());
			}
		} catch (NMException e) {
			logger.trace(e.getMessage());
			fail("should not get this exception: " + e.getMessage());
		}
	}



	@Test
	public void invalidRequest() throws NMException{
		String requestId = "NonExistingRequest";
		Thread nmsThread = new Thread(nms);
		nmsThread.start();
		try {
			metricsDAO.getRequestStatus(requestId);
		} catch (NMException ex){
			nms.stop();
			return;
		}
		fail("Should get NMException");
	}
}
