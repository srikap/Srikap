package org.myorg.monitor;


/**
 * Runnable interface allows the service to run in a thread if needed,
 * and thus allows for unit testing with JUnit
 * 
 * @author srikap
 *
 */
public interface NodeMonitorService extends Runnable{
	/**
	 * Main Service method.
	 */
	void run();

	/**
	 * Show allow to stop the service
	 */
	void stop();

}
