package edu.upenn.cis.cis455.m1.handling;

import java.util.Hashtable;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.upenn.cis.cis455.Constants;
import edu.upenn.cis.cis455.exceptions.HaltException;
import edu.upenn.cis.cis455.m2.interfaces.Request;
import edu.upenn.cis.cis455.m2.interfaces.Response;
import edu.upenn.cis.cis455.m1.server.HttpTask;
import edu.upenn.cis.cis455.m1.server.HttpWorker;
import edu.upenn.cis.cis455.m1.server.RequestFactory;
import edu.upenn.cis.cis455.m1.server.RequestObj;
import edu.upenn.cis.cis455.m1.server.ResponseObj;
import edu.upenn.cis.cis455.m2.server.WebService;
import edu.upenn.cis.cis455.utils.SocketOutputBodyBuilder;

/**
 * Handles marshalling between HTTP Requests and Responses
 */
public class HttpIoHandler {
    final static Logger logger = LogManager.getLogger(HttpIoHandler.class);

    private ResponseObj successResponse = new ResponseObj();
    public Hashtable<String,String> _parsedHeaders;
    public Hashtable<String,List<String>> _parsedQueryParams;
    public String _uri;
    private InetAddress _remoteIp;
    private Socket _socket;
    private HttpTask _httpTask;
    
    public HttpIoHandler(Socket socket, HttpTask task) {
    	this._socket = socket;
    	this._httpTask = task;
    }
    
    public boolean handleRequest() throws HaltException, Exception {
    	
		// Parses the input stream and sets values to _parsedHeaders and _uri
    	if (!parseInputStream()) {
    		return false;
    	}
    	
    	// Creates a new request based on type w/ RequestFactory
    	Request request = createRequest();
    	WebService.getInstance().threadStatuses.put(Thread.currentThread().getName(),request.uri());
    	
		// Call Request Handler to handle the request
		RequestHandler requestHandler = new RequestHandler(request, successResponse, _socket);
		requestHandler.handleRequest();
		
		outputResponseToSocket(successResponse);
		return true;
    		
    }
    
    /**
     * Parses the socket's InputStream and gets the headers as well as the uri
     */
    private boolean parseInputStream() {
		try {
			
	    	Hashtable<String, String> headers = new Hashtable<String,String>();
	    	Hashtable<String, List<String>> parms = new Hashtable<String,List<String>>();
	    	
	    	// Get the client's IP address if available
	    	String remoteIp = "";
	    	if (_socket.getInetAddress() != null) {
	    		remoteIp = _socket.getInetAddress().toString();
	    	}
	    	
	    	// If there was a bad request, then throw a 400 error
	    	try {
	    		_uri = HttpParsing.parseRequest(remoteIp, _socket.getInputStream(), headers, parms);
	    	} catch(HaltException he) {
	    		successResponse.status(400);
	    		outputResponseToSocket(successResponse);
	    		return false;
	    	}
	    	
	    	this._parsedHeaders = headers;
	    	this._parsedQueryParams = parms;
	    	
		} catch (IOException e) {
			System.out.println("Error reading socket input stream" + e.toString());
		} catch (HaltException he) {
			logger.error("Bad request");
			
		}
		return true;
    }
    
    private void outputResponseToSocket(Response response) throws IOException {
    	// Build the output to the socket
    			SocketOutputBodyBuilder socketOutputBuilder = new SocketOutputBodyBuilder();
    			byte[] socketOutputBytes = socketOutputBuilder.buildSocketOutput(successResponse);
    			sendResponse(_socket, socketOutputBytes);
    }
    
    // Use the RequestFactory to build a request
    private Request createRequest() throws IOException {
		RequestFactory requestFactory = new RequestFactory();
    	return requestFactory.getRequest(_parsedHeaders, _httpTask, _uri, _parsedQueryParams);
	}

    /**
     * Sends an exception back, in the form of an HTTP response code and message.
     * Returns true if we are supposed to keep the connection open (for persistent
     * connections).
     * @throws IOException 
     */
    public static boolean sendException(Socket socket, Request request, HaltException except) throws IOException {
    	if (!request.persistentConnection()) {
    		OutputStream outputStream = socket.getOutputStream();
    		outputStream.write(except.body().getBytes());
    	}
    	return true;
    }

    /**
     * Sends data back. Returns true if we are supposed to keep the connection open
     * (for persistent connections).
     * @throws IOException 
     */
	public static boolean sendResponse(Socket socket, byte[] socketOutputBytes) throws IOException {
    	//if (!request.persistentConnection()) {
    		// Write output to socket
        	OutputStream outputStream = socket.getOutputStream();
        	outputStream.write(socketOutputBytes); 
    	//}
    	return true;
        
    }
}
