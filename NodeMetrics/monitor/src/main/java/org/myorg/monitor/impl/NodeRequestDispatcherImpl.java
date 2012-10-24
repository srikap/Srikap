package org.myorg.monitor.impl;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.myorg.main.NodeMonitorServiceConfigurator;
import org.myorg.monitor.NodeCommand;
import org.myorg.monitor.NodeRequestDispatcher;

/**
 * Dispatcher uses a thread pool. Thread Pool type used can be a Strategy.
 * @author srikap
 *
 */
public class NodeRequestDispatcherImpl implements NodeRequestDispatcher{
	private Integer poolSize = NodeMonitorServiceConfigurator.getInstance().getIntProperty("org.myorg.monitor.nodeLimitedRequestDispatcher.poolSize",10);
	Integer maxPoolSize = NodeMonitorServiceConfigurator.getInstance().getIntProperty("org.myorg.monitor.nodeLimitedRequestDispatcher.maxPoolSize",200);
	Long keepAliveTime = NodeMonitorServiceConfigurator.getInstance().getLongProperty("org.myorg.monitor.nodeLimitedRequestDispatcher.keepAliveTime",10);

	ThreadPoolExecutor threadPool = null;
	private Logger logger = Logger.getLogger(NodeRequestDispatcherImpl.class);

	public NodeRequestDispatcherImpl()
	{
		logger.info("poolsize:" + poolSize + " maxPoolSize:"+maxPoolSize + " keepAliveTime:" + keepAliveTime);
		threadPool = new ThreadPoolExecutor(poolSize, maxPoolSize,
				keepAliveTime, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(1000));
		logger.trace("Threadpool created.");
	}

	public void runTask(NodeCommand nodeCommand) {
		logger.trace("Executing the nodeCommand");
		threadPool.execute(nodeCommand);
	}

}

