package edu.upenn.cis.cis455.generalRequestTests;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests functionality when user is logged in (has valid Session) and makes requests
 * @author vagrant
 *
 */
public class RequestPageLoggedInTestTest {

	// Test1: User is logged in and makes request to root URL
			// Returns 200 OK w/ "Welcome <user>"
	// Test2: User is logged in and makes request to /index.html
			// Returns 200 OK w/ "Welcome <user>"
	// Test3: User is logged in and makes request to path or directory that doesn't exist
			// Returns 404 error
	// Test4: User is logged in and makes request to path that does exist
			// Returns 200 OK w/ required files
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
