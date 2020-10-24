package edu.upenn.cis.cis455.storage.sampleDatabase;

import java.io.Serializable;

public class SupplierKey implements Serializable
{
    private String number;

    public SupplierKey(String number)
    {
        this.number = number;
    }

    public final String getNumber()
    {
        return number;
    }

    public String toString()
    {
        return "[SupplierKey: number=" + number + ']';
    }
} 
