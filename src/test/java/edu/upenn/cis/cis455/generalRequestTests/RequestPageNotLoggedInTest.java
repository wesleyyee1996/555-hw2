package edu.upenn.cis.cis455.generalRequestTests;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Tests if the user is not logged in but tries to request a page
 * These tests assume that the request is valid
 * @author vagrant
 *
 */
public class RequestPageNotLoggedInTest {

	// Test1: User is not logged in and doesn't have a session Id cookie
			// Returns the login page
	// Test2: User is not logged in and makes request to /login
			// Returns the login page
	// Test3: User is not logged in and makes request to /register
			// Returns the register page
	// Test4: User is not logged in and makes request to any other page
			// Returns the login page
	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
