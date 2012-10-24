package org.myorg.monitor;


/**
 * Abstract Factory
 * @author srikap
 *
 */
public interface NodeCommandFactory {
	/**
	 * Should create a NodeCommand
	 * @param nodeName
	 * @return
	 */
	NodeCommand createNodeCommand(String nodeName);
}
