package org.myorg.exception;

import org.apache.log4j.Logger;

public class NMSUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private Logger logger = Logger.getLogger(NMSUncaughtExceptionHandler.class);
	public void uncaughtException(Thread t, Throwable e) {

		logger.fatal(e.getMessage());
		for (StackTraceElement ste:e.getStackTrace()){
			logger.fatal("filename;" + ste.getFileName() + " line:" +ste.getLineNumber() + " method:" +  ste.getMethodName());
		}
		logger.fatal(e.getStackTrace());
	}
}