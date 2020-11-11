package com.uncc.kdd;

import java.util.*;

// This class for a group of attributes, represents a set of entries .
public class AttrGroups 
{
	// if AttrGroups is a subset of one of the decision attribute's classes then mark will be True.
    public Set<String> mark = null;
    public Set<Integer> entry;
    public ArrayList<String> attValues;

    // constructor to initialize variables
    public AttrGroups(Set<Integer> entry, ArrayList<String> attValues) 
    {
    	this.entry = entry;
    	this.attValues = attValues;
        
    }

    @Override
    public int hashCode() 
    {
        return 0;
    }
    
    public int getCardi() 
    {
        return entry.size();
    }
   
    @Override
    public boolean equals(Object o) 
    {
        if (!(o instanceof AttrGroups)) 
        {
            return false;
        }
        
        AttrGroups z = (AttrGroups) o;

        return this.attValues.equals(z.attValues);
    }
    
    // this function returns true if the object is a subset of the other 
    public boolean isSubset(AttrGroups o) 
    {
        // The object is a subset of other if every attValues in the current object is found in other.
        for (int j = 0; j < this.attValues.size(); j++) 
        {
            String cItem = this.attValues.get(j);
            if (cItem == null)
                continue;

            String oItem = o.attValues.get(j);
            if (!cItem.equals(oItem)) 
            {
                return false;
            }
        }
        return true;
    }

    // Combining two Attribute Groups
    public static AttrGroups comb(AttrGroups x, AttrGroups y, int dSize) 
    {
        int numAttr = x.attValues.size();

        // Grow Attribute Groups to the desired size.
        ArrayList<String> newAttrValues = new ArrayList<>();

        int diffNum = 0;
        int sameNum = 0;
        
        for (int j = 0; j < numAttr; j++) 
        {
        	String yVal = y.attValues.get(j);
        	String xVal = x.attValues.get(j);

            // if both x and y are not present 
            if (yVal == null && xVal == null) 
            {
                newAttrValues.add(null);
                continue;
            }
            if (yVal == null) 
            {
                newAttrValues.add(xVal);
                diffNum++;

            } 
            else if (xVal == null) 
            {
                newAttrValues.add(yVal);
                diffNum++;

            } 
            else 
            {
                // if xVal and yVal both exist and are the same.
                if (yVal.equals(xVal)) 
                {
                    newAttrValues.add(xVal);
                    sameNum++;
                } 
                else 
                {
                    return null;
                }
            }
        }

        //  desired size must be equal to sameNum + diffNum.
        int tSize = sameNum + diffNum;
        if (tSize != dSize)
            return null;

        Set<Integer> newEntry = new TreeSet<>();
        for (Integer f : x.entry) 
        {
            if (y.entry.contains(f)) 
            {
                newEntry.add(f);
            }
        }

        return new AttrGroups(newEntry, newAttrValues);
    }
}
