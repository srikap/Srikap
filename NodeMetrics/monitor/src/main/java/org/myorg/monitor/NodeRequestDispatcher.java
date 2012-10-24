package org.myorg.monitor;
/**
 * Executes/dispatches the request to remote node
 * @author srikap
 *
 */

public interface NodeRequestDispatcher {
	/**
	 * executes command
	 * @param nodeCommand
	 */
	public void runTask(NodeCommand nodeCommand);
}
