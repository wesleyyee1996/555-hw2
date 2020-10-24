package edu.upenn.cis.cis455.storage.sampleDatabase;

import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialSerialKeyCreator;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.ForeignKeyDeleteAction;
import com.sleepycat.je.RunRecoveryException;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.util.ExceptionUnwrapper;

import java.io.File;
import java.io.FileNotFoundException;

public class SampleDatabase
{
    private Environment env;
    private static final String CLASS_CATALOG = "java_class_catalog";
    private static final String SUPPLIER_STORE = "supplier_store";
    private static final String PART_STORE = "part_store";
    private static final String SHIPMENT_STORE = "shipment_store";
    
    private static final String SHIPMENT_PART_INDEX = "shipment_part_index";
    private static final String SHIPMENT_SUPPLIER_INDEX = "shipment_supplier_index";
    
    private StoredClassCatalog javaCatalog;
    private Database supplierDb;
    private Database partDb;
    private Database shipmentDb;
    private SecondaryDatabase shipmentByPartDb;
    private SecondaryDatabase shipmentBySupplierDb;
    
    public SampleDatabase(String homeDirectory)
            throws DatabaseException, FileNotFoundException
        {
    	
    	try {
    		
    		// Creates the environment for all databases and sets configs
            System.out.println("Opening environment in: " + homeDirectory);
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setTransactional(true);
            envConfig.setAllowCreate(true);
            env = new Environment(new File(homeDirectory), envConfig);
            
            // Used to specify configuration parameters when opening the database
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setTransactional(true);
            dbConfig.setAllowCreate(true);
            
            // Opens database w/ auto-commit, named 'CLASS_CATALOG', w/ configuration dbConfig
            Database catalogDb = env.openDatabase(null, CLASS_CATALOG, dbConfig); 
            // Since ClassCatalog is special, we store it into a different object
            javaCatalog = new StoredClassCatalog(catalogDb);
            
            // Create different databases w/ auto-commit, w/ configuration dbConfig
            partDb = env.openDatabase(null, PART_STORE, dbConfig);
            supplierDb = env.openDatabase(null, SUPPLIER_STORE, dbConfig);
            shipmentDb = env.openDatabase(null, SHIPMENT_STORE, dbConfig);
            
            // Initialize the secondary config to store the FK table
            // Everything the same as dbConfig, but make sure to set setSortedDuplicates to true to allow duplicate index keys
            SecondaryConfig secConfig = new SecondaryConfig();
            secConfig.setTransactional(true);
            secConfig.setAllowCreate(true);
            secConfig.setSortedDuplicates(true);
            
            // 
            secConfig.setForeignKeyDatabase(partDb);
            secConfig.setForeignKeyDeleteAction(
                                         ForeignKeyDeleteAction.CASCADE);
	        secConfig.setKeyCreator(
	            new ShipmentByPartKeyCreator(javaCatalog,
	                                         ShipmentKey.class,
	                                         ShipmentData.class,
	                                         PartKey.class));
	        shipmentByPartDb = env.openSecondaryDatabase(null, 
	                                                     SHIPMENT_PART_INDEX,
	                                                     shipmentDb,
	                                                     secConfig);
	
	        secConfig.setForeignKeyDatabase(supplierDb);
	        secConfig.setForeignKeyDeleteAction(
	                                         ForeignKeyDeleteAction.CASCADE);
	        secConfig.setKeyCreator(
	            new ShipmentBySupplierKeyCreator(javaCatalog,
	                                             ShipmentKey.class,
	                                             ShipmentData.class,
	                                             SupplierKey.class));
	        shipmentBySupplierDb = env.openSecondaryDatabase(null,
	                                                SHIPMENT_SUPPLIER_INDEX,
	                                                shipmentDb,
	                                                secConfig);
    	}
    	catch (Exception e)
        {
            e = ExceptionUnwrapper.unwrap(e);
            if (e instanceof RunRecoveryException)
            {
                // follow recovery procedure
            }
        } 
        } 
    
    public final Database getPartDatabase()
    {
        return partDb;
    }

    public final Database getSupplierDatabase()
    {
        return supplierDb;
    }

    public final Database getShipmentDatabase()
    {
        return shipmentDb;
    }
    
    public final StoredClassCatalog getClassCatalog() {
    	return javaCatalog;
    }
    
    public final Environment getEnvironment() {
    	return env;
    }
    
    public final SecondaryDatabase getShipmentByPartDatabase()
    {
        return shipmentByPartDb;
    }

    public final SecondaryDatabase getShipmentBySupplierDatabase()
    {
        return shipmentBySupplierDb;
    }

    public void close()
        throws DatabaseException
    {
    	shipmentByPartDb.close();
        shipmentBySupplierDb.close();
    	partDb.close();
        supplierDb.close();
        shipmentDb.close();
        
        // closes the underlying class catalog database
    	javaCatalog.close();
    	env.close();
    }
    
    // Used to configure secondary database objects
    private static class ShipmentByPartKeyCreator
	    extends SerialSerialKeyCreator
	{
	    private ShipmentByPartKeyCreator(ClassCatalog catalog,
	                                     Class primaryKeyClass,
	                                     Class valueClass,
	                                     Class indexKeyClass)
	    {
	        super(catalog, primaryKeyClass, valueClass, indexKeyClass);
	    }
	
	    public Object createSecondaryKey(Object primaryKeyInput,
	                                     Object valueInput)
	    {
	        ShipmentKey shipmentKey = (ShipmentKey) primaryKeyInput;
	        return new PartKey(shipmentKey.getPartNumber());
	    }
	}
	
 // Used to configure secondary database object 
	private static class ShipmentBySupplierKeyCreator
	    extends SerialSerialKeyCreator
	{
	    private ShipmentBySupplierKeyCreator(ClassCatalog catalog,
	                                         Class primaryKeyClass,
	                                         Class valueClass,
	                                         Class indexKeyClass)
	    {
	        super(catalog, primaryKeyClass, valueClass, indexKeyClass);
	    }
	
	    public Object createSecondaryKey(Object primaryKeyInput,
	                                     Object valueInput)
	    {
	        ShipmentKey shipmentKey = (ShipmentKey) primaryKeyInput;
	        return new SupplierKey(shipmentKey.getSupplierNumber());
	    }
	}
} 
