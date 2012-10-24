package org.myorg.monitor.impl;

import org.apache.log4j.Logger;
import org.myorg.db.MetricsDAO;
import org.myorg.monitor.NodeCommand;
import org.myorg.monitor.NodeCommandFactory;
import org.myorg.monitor.NodeMonitorService;
import org.myorg.monitor.NodeRequestDispatcher;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Main Service class.
 * @author srikap
 *
 */
public class NodeMonitorServiceImpl implements NodeMonitorService{
	@Inject private NodeRequestDispatcher nodeRequestDispatcher = null;
	@Inject private NodeCommandFactory nodeCommandFactory = null;
	@Inject private MetricsDAO metricsDAO = null;
	private boolean stop = false;
	private Logger logger = Logger.getLogger(NodeMonitorServiceImpl.class);

	public static void main(String[] args){
		Injector injector = Guice.createInjector(new NmsConfigModule());
		NodeMonitorService nms = injector.getInstance(NodeMonitorService.class);
		nms.run();
	}

	/* (non-Javadoc)
	 * @see org.myorg.monitor.NodeMonitorService#run()
	 */
	public void run(){
		while (!stop){
			String node = metricsDAO.getNodenameRequested();
			if(node != null){
				//Will block till a threads is available
				NodeCommand nc = nodeCommandFactory.createNodeCommand(node);
				nodeRequestDispatcher.runTask(nc);
			} else {
				try {
					logger.trace("No requests pending. Sleeping for 2 seconds");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					logger.fatal("Interrupted: " + e.getMessage());
				}
			}
		}
	}
	


	public NodeRequestDispatcher getNodeRequestDispatcher() {
		return nodeRequestDispatcher;
	}


	public void setNodeRequestDispatcher(NodeRequestDispatcher nodeRequestDispatcher) {
		this.nodeRequestDispatcher = nodeRequestDispatcher;
	}


	public NodeCommandFactory getNodeCommandFactory() {
		return nodeCommandFactory;
	}


	public void setNodeCommandFactory(NodeCommandFactory nodeCommandFactory) {
		this.nodeCommandFactory = nodeCommandFactory;
	}


	public MetricsDAO getMetricsDAO() {
		return metricsDAO;
	}


	public void setMetricsDAO(MetricsDAO x) {
		metricsDAO = x;
	}

	public void stop() {
		this.stop = true;
	}
	
}
