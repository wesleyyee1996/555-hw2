package edu.upenn.cis.cis455.loginPageTests;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test that can create a new account
 * @author vagrant
 *
 */
public class CreateNewAccountTest {

	// Send a POST localhost:45555/register?username=testUser&password=testPass123
	
	// Test1: request is valid and username + password are good
			// should return 200 OK w/ link to main page and its login screen
	// Test2: request is valid, but username is already good 
			// shouldn't we return a message that is a 200 OK, but says username already exists?
	// Test3: request is invalid and username + password are good
			// should return a 400 error for bad request
	
	// IF TIME PERMITS--Test3: max # of logins
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
