package edu.upenn.cis.cis455.storage.sampleDatabase;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredEntrySet;
import com.sleepycat.collections.StoredSortedMap;


/**
 * This is the class that creates bindings or maps between different tables
 * @author vagrant
 *
 */
public class SampleViews
{
    private StoredSortedMap<PartKey, PartData> partMap;
    private StoredSortedMap<SupplierKey, PartData> supplierMap;
    private StoredSortedMap<ShipmentKey, PartData> shipmentMap;
    
    private StoredSortedMap supplierByCityMap;
    private StoredSortedMap shipmentByPartMap;
    private StoredSortedMap shipmentBySupplierMap;

    
    public SampleViews(SampleDatabase db)
    {
    	ClassCatalog catalog = db.getClassCatalog();
    	
    	// Creates key and data bindings
    	// First parameter is class catalog and used to store class descriptions of serialized objects
    	// Second param is base class for serialized objects and used for type checking of stored objects
    		// if null or Object.class specified, then any Java class is allowed
			// otherwise, all objects stored in that format must be instances of specified class or derived from it
        EntryBinding<PartKey> partKeyBinding =
            new SerialBinding<PartKey>(catalog, PartKey.class);
        EntryBinding<PartData> partDataBinding =
            new SerialBinding<PartData>(catalog, PartData.class);
        EntryBinding<SupplierKey> supplierKeyBinding =
            new SerialBinding<SupplierKey>(catalog, SupplierKey.class);
        EntryBinding<SupplierData> supplierDataBinding =
            new SerialBinding<SupplierData>(catalog, SupplierData.class);
        EntryBinding<ShipmentKey> shipmentKeyBinding =
            new SerialBinding<ShipmentKey>(catalog, ShipmentKey.class);
        EntryBinding<ShipmentData> shipmentDataBinding =
            new SerialBinding<ShipmentData>(catalog, ShipmentData.class);
        
        EntryBinding cityKeyBinding =
                new SerialBinding(catalog, String.class);
        
        supplierByCityMap =
                new StoredSortedMap(db.getSupplierByCityDatabase(),
                              cityKeyBinding, supplierDataBinding, true);
            shipmentByPartMap =
                new StoredSortedMap(db.getShipmentByPartDatabase(),
                              partKeyBinding, supplierDataBinding, true);
            shipmentBySupplierMap =
                new StoredSortedMap(db.getShipmentBySupplierDatabase(),
                              supplierKeyBinding, supplierDataBinding, true); 
        
        // Creates standard java maps using StoredSortedMaps
        // 1st param: database. in StoredSortedMap, DB primary keys used as map keys
        // 2nd param: key binding to use when storing/retrieving objects via the map
        // 3rd param: value binding to use when storing/retrieving objects via the map
        // 4th param: specifies whether read only or not (false = read only)
        partMap =
            new StoredSortedMap(db.getPartDatabase(),
                          partKeyBinding, partDataBinding, true);
        supplierMap =
            new StoredSortedMap(db.getSupplierDatabase(),
                          supplierKeyBinding, supplierDataBinding, true);
        shipmentMap =
            new StoredSortedMap(db.getShipmentDatabase(),
                          shipmentKeyBinding, shipmentDataBinding, true);
    }
    
    public final StoredSortedMap getShipmentByPartMap()
    {
        return shipmentByPartMap;
    }

    public final StoredSortedMap getShipmentBySupplierMap()
    {
        return shipmentBySupplierMap;
    }

    public final StoredSortedMap getSupplierByCityMap()
    {
        return supplierByCityMap;
    }

    public final StoredEntrySet getShipmentByPartEntrySet()
    {
        return (StoredEntrySet) shipmentByPartMap.entrySet();
    }

    public final StoredEntrySet getShipmentBySupplierEntrySet()
    {
        return (StoredEntrySet) shipmentBySupplierMap.entrySet();
    }

    public final StoredEntrySet getSupplierByCityEntrySet()
    {
        return (StoredEntrySet) supplierByCityMap.entrySet();
    }
    
    public final StoredSortedMap<PartKey, PartData> getPartMap()
    {
        return partMap;
    }

    public final StoredSortedMap<SupplierKey, PartData> getSupplierMap()
    {
        return supplierMap;
    }

    public final StoredSortedMap<ShipmentKey, PartData> getShipmentMap()
    {
        return shipmentMap;
    }

    public final StoredEntrySet getPartEntrySet()
    {
        return (StoredEntrySet) partMap.entrySet();
    }

    public final StoredEntrySet getSupplierEntrySet()
    {
        return (StoredEntrySet) supplierMap.entrySet();
    }

    public final StoredEntrySet getShipmentEntrySet()
    {
        return (StoredEntrySet) shipmentMap.entrySet();
    }

} 
