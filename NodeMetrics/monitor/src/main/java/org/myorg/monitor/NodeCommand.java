package org.myorg.monitor;

/**
 * NodeCommand encapsulates the Command to fetch the node metrics from node.
 * @author srikap
 *
 */
public interface NodeCommand extends Runnable{
	/**
	 * gets data from node in JSON format and persists it.
	 * @param node
	 * @return
	 */
	public void run();

	/**
	 * fqdn or ipaddress
	 * @param nodeName
	 */
	public void setNodeName(String nodeName);
}