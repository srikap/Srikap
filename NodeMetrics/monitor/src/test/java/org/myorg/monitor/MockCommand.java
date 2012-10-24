package org.myorg.monitor;

import org.myorg.db.RequestStatus;
import org.myorg.monitor.impl.NodeCommandImpl;

/**
 * To Mock remote node
 * @author srikap
 *
 */
public class MockCommand extends NodeCommandImpl {
	static final String statusMessage = "{\"cpu\": { \"core1\": \"80%\", \"core2\": \"33%\"},\"mem\": {\"used\": \"1234M\", \"free\": \"6666M\"}}\"";

	@Override
	public void run(){
		setStatus(statusMessage, RequestStatus.COMPLETED);
	}
}
