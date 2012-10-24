package org.myorg.monitor.impl;


import org.myorg.monitor.NodeCommand;
import org.myorg.monitor.NodeCommandFactory;

import com.google.inject.Inject;
/**
 * Factory class to construct NodeCommands.
 * @author srikap
 *
 */
public class NodeCommandFactoryImpl implements NodeCommandFactory{
	@Inject NodeCommand nc = null;
	public NodeCommand createNodeCommand(String nodeName) {
		nc.setNodeName(nodeName);
		return nc;
	}
}
