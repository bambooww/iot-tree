package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class IOTTLoggerFactory implements ILoggerFactory
{

	ConcurrentMap<String, Logger> loggerMap;

    public IOTTLoggerFactory() {
    	
    	
        loggerMap = new ConcurrentHashMap<String, Logger>();
        IOTTLogger.lazyInit();
        
        System.out.println("iott log factory inited") ;
    }

    /**
     * Return an appropriate {@link SimpleLogger} instance by name.
     */
    public Logger getLogger(String name) {
        Logger simpleLogger = loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        } else {
        	ILogger ilog = LoggerManager.getLogger(name) ;
            Logger newInstance = (Logger)ilog;//new IOTTLogger(ilog,name);
            Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    /**
     * Clear the internal logger cache.
     *
     * This method is intended to be called by classes (in the same package) for
     * testing purposes. This method is internal. It can be modified, renamed or
     * removed at any time without notice.
     *
     * You are strongly discouraged from calling this method in production code.
     */
    void reset() {
        loggerMap.clear();
    }
}
