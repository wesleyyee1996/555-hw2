/**
 * CIS 455/555 route-based HTTP framework.
 * 
 * Basic service handler and route manager.
 * 
 * V. Liu, Z. Ives
 * 
 * Portions excerpted from or inspired by Spark Framework, 
 * 
 *                 http://sparkjava.com,
 * 
 * with license notice included below.
 */

/*
 * Copyright 2011- Per Wendel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.upenn.cis.cis455;

import java.io.IOException;

import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m1.server.HttpTask;
import edu.upenn.cis.cis455.m2.interfaces.Filter;
import edu.upenn.cis.cis455.m2.interfaces.Session;

// change all to edu.upenn.cis.cis455.m2 for m2
import edu.upenn.cis.cis455.m2.server.WebService;
import edu.upenn.cis.cis455.m2.interfaces.Route;

public class WebServiceFactory {
	
	private int _threadPoolSize;
	private static WebService _webService;

    // We don't want people to use the constructor
    protected WebServiceFactory() {}
    
    public static void createWebService() {
    	_webService = new WebService();
    }
    
    /**
     * Handle an HTTP GET request to the path
     */
    public static void get(String path, Route route) {
        _webService.get(path, route);
    }

    /**
     * Handle an HTTP POST request to the path
     */
    public static void post(String path, Route route) {
        _webService.post(path, route);
    }

    /**
     * Handle an HTTP PUT request to the path
     */
    public static void put(String path, Route route) {
    	_webService.put(path, route);
    }

    /**
     * Handle an HTTP DELETE request to the path
     */
    public static void delete(String path, Route route) {
    	_webService.delete(path, route);
    }

    /**
     * Handle an HTTP HEAD request to the path
     */
    public static void head(String path, Route route) {
    	_webService.head(path, route);
    }

    /**
     * Handle an HTTP OPTIONS request to the path
     */
    public static void options(String path, Route route) {
    	_webService.options(path, route);
    }

    ///////////////////////////////////////////////////
    // HTTP request filtering
    ///////////////////////////////////////////////////

    /**
     * Add filters that get called before a request
     */
    public static void before(Filter... filters) {
    	for (Filter filter : filters){
    		_webService.before(filter);
    	}
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(Filter... filters) {
    	for (Filter filter : filters) {
            _webService.after(filter);
    	}
    }

    /**
     * Add filters that get called before a request
     */
    public static void before(String path, String acceptType, Filter... filters) {
    	for (Filter filter : filters) {
            _webService.before(path, acceptType, filter);
    	}
    }

    /**
     * Add filters that get called after a request
     */
    public static void after(String path, String acceptType, Filter... filters) {
    	for (Filter filter : filters) {
            _webService.after(path, acceptType, filter);
    	}
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt() {
        return _webService.halt();
    }

    /**
     * Triggers a HaltException that terminates the request
     */
    public static HaltException halt(int statusCode, String body) {
        return _webService.halt(statusCode, body);
    }

    ////////////////////////////////////////////
    // Server configuration
    ////////////////////////////////////////////

    /**
     * Set the IP address to listen on (default 0.0.0.0)
     */
    public static void ipAddress(String ipAddress) {
        _webService.ipAddress(ipAddress);
    }

    /**
     * Set the port to listen on (default 45555)
     */
    public static void port(int port) {
        _webService.port(port);
    }

    /**
     * Set the size of the thread pool
     */
    public static void threadPool(int threads) {
        _webService.threadPoolSize(threads);
    }

    /**
     * Set the root directory of the "static web" files
     */
    public static void staticFileLocation(String directory) {
        _webService.staticFileLocation(directory);
    }

    /**
     * Hold until the server is fully initialized
     */
    public static void awaitInitialization() {
        _webService.awaitInitialization();
    }

    /**
     * Gracefully shut down the server
     */
    public static void stop() {
        _webService.stop();
    }

    public static String createSession() {
        return _webService.createSession();
    }

    public static Session getSession(String id) {
        throw new UnsupportedOperationException();
    }
    
    public static void addToTaskQueue(HttpTask task) {
    	_webService.addToTaskQueue(task);
    }
}
