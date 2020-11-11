package com.uncc.kdd;

import java.util.Arrays;
import java.util.List;

// This class is used to represent action rules. dtFP contains information about transformations. 
public class ActRules 
{
	//variables
	private double confi;
	private int suppo;
	
	private String[] dtFP;
    private List<String> aName;
    private List<String[]> tFP;

    //constructor to initialize variables
    public ActRules(double confi, int suppo, String[] dtFP, List<String> aName, List<String[]> tFP) 
    {
    	this.confi = confi;
    	this.suppo = suppo;
    	
    	this.dtFP = dtFP;
        this.aName = aName;
        this.tFP = tFP;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (!(o instanceof ActRules)) 
        {
            return false;
        }

        ActRules r = (ActRules) o;

        if (r.tFP.size() != this.tFP.size()) 
        {
            return false;
        }
        for (int j = 0; j < this.tFP.size(); j++) 
        {
            if (!Arrays.equals(r.tFP.get(j), this.tFP.get(j))) 
            {
                return false;
            }
        }     
        return true;
    }
    
    @Override
    public int hashCode() 
    {
        int val = tFP.get(0)[0].hashCode();
        return val;
    }
    
    public String toMdString() 
    {
        // (b: 1 -> 2 ^ a = 5) -> (c: 7 -> 9)
        // (b1, a5) -> c6 -- Support:2 -- Confidence 50%
        StringBuilder strbuild = new StringBuilder();
        strbuild.append("| (");
        
        for (int j = 0; j < this.tFP.size(); j++) 
        {
            String[] cTerm = this.tFP.get(j);
            // index of attribute is the first index.
            int aIndex = Integer.parseInt(cTerm[0]);
            // FROM value is the second index.
            String fVal = cTerm[1];
            // TO value is the third index.
            String tVal = cTerm[2];

            if (fVal.equals(tVal)) 
            {
            	// values are equal so the attribute is stable.
                // format is x = 7,
                strbuild.append(aName.get(aIndex));
                strbuild.append(" = ");
                strbuild.append(fVal);
            } 
            else 
            {
            	// values are not equal so the attribute is flexible
                // format is x: 2 --> 4
                strbuild.append(aName.get(aIndex));
                strbuild.append(": ");
                strbuild.append(fVal);
                strbuild.append(" --> ");
                strbuild.append(tVal);
            }
            strbuild.append(", ");
        }
        // Removing comma and space at the end
        strbuild.delete(strbuild.length() - 2, strbuild.length() - 1);
        strbuild.append(")");
        strbuild.append(" --> ");

        // Adding decision variable aName 
        strbuild.append("(");
        strbuild.append(aName.get(Integer.parseInt(this.dtFP[0])));
        strbuild.append(": ");
        strbuild.append(this.dtFP[1]);
        strbuild.append(" --> ");
        strbuild.append(this.dtFP[2]);
        strbuild.append(")");

        strbuild.append(String.format(" | %d | %.2f %%|", this.suppo, this.confi * 100));

        return strbuild.toString();
    }

    @Override
    public String toString() 
    {
        // (b: 1 -> 2 ^ a = 5) -> (c: 7 -> 9)
        // (b1, a5) -> c6 -- Support:2 -- Confidence 50%
        StringBuilder strbuild = new StringBuilder();
        strbuild.append("(");
        
        for (int j = 0; j < this.tFP.size(); j++) 
        {
            String[] cTerm = this.tFP.get(j);
            // index of attribute is the first index.
            int aIndex = Integer.parseInt(cTerm[0]);
            // FROM value is the second index.
            String fVal = cTerm[1];
            // TO value is the third index.
            String tVal = cTerm[2];

            if (fVal.equals(tVal)) 
            {
            	// values are equal so the attribute is stable.
                // format is x = 7,
                strbuild.append(aName.get(aIndex));
                strbuild.append(" = ");
                strbuild.append(fVal);
            } 
            else 
            {
                // values are not equal so the attribute is flexible
                // format is x: 2 --> 4
                strbuild.append(aName.get(aIndex));
                strbuild.append(": ");
                strbuild.append(fVal);
                strbuild.append(" --> ");
                strbuild.append(tVal);
            }
            strbuild.append(", ");
        }

        // Removing comma and space at the end
        strbuild.delete(strbuild.length() - 2, strbuild.length() - 1);
        strbuild.append(")");
        strbuild.append(" --> ");

        // Adding decision variable aName 
        strbuild.append("(");
        strbuild.append(aName.get(Integer.parseInt(this.dtFP[0])));
        strbuild.append(": ");
        strbuild.append(this.dtFP[1]);
        strbuild.append(" --> ");
        strbuild.append(this.dtFP[2]);
        strbuild.append(")");

        strbuild.append(String.format(" #### SUPPORT: %d #### CONFIDENCE: %.2f percent", this.suppo, this.confi * 100));

        return strbuild.toString();
    }
}
