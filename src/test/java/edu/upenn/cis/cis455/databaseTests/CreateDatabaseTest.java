package edu.upenn.cis.cis455.databaseTests;

import org.junit.Test;

import edu.upenn.cis.cis455.storage.sampleDatabase.Sample;
import edu.upenn.cis.cis455.storage.sampleDatabase.SampleDatabase;
import edu.upenn.cis.cis455.storage.sampleDatabase.SampleViews;

public class CreateDatabaseTest {

	// Test2: Test that able to add file to database
	// Test3: Test that able to 
	
	// Test1: Test that able to create a database at given location
	@Test
	public void test1() {
		String homeDirectory = "/vagrant/555-hw2/database";
		
		Sample.main(new String[] {"-h",homeDirectory});
	}

}
