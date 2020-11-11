package com.uncc.kdd;

import java.util.List;

// This is a LERS representation of a rule
public class Rules 
{
	public AttrGroups deciGroup;
    public AttrGroups attrGroup;

    private List<String> attrName;
    
    private int suppo;
    private double confi;

    // constructor to initialize variables
    public Rules(int suppo, double confi, List<String> attrName, AttrGroups attrGroup, AttrGroups deciGroup) 
    {
        this.attrGroup = attrGroup;
        this.deciGroup = deciGroup;
        this.confi = confi;
        this.suppo = suppo;
        this.attrName = attrName;
    }
    
    @Override
    public int hashCode() 
    {
        return 0;
    }
    
    public double getConfi() 
    {
        return confi;
    }

    public int getSuppo() 
    {
        return suppo;
    }

    @Override
    public boolean equals(Object o) 
    {
        if (!(o instanceof Rules)) 
        {
            return false;
        }
        
        Rules ru = (Rules) o;

        return this.deciGroup.equals(ru.deciGroup) && this.attrGroup.equals(ru.attrGroup);
    }
    
    public String toMdString() 
    {
        // (b1, a5) -> c6 -- Support:2 -- Confidence 50%
        StringBuilder strbuild = new StringBuilder();
        strbuild.append("| (");
        
        for (int i = 0; i < this.attrGroup.attValues.size(); i++) 
        {
            String cVal = this.attrGroup.attValues.get(i);
            String cName = this.attrName.get(i);
            if (cVal != null) 
            {
                strbuild.append(cName);
                strbuild.append(" - ");
                strbuild.append(cVal);
                strbuild.append(", ");
            }
        }

        // Removing comma and space at the end
        strbuild.delete(strbuild.length() - 2, strbuild.length() - 1);
        strbuild.append(")");
        strbuild.append(" --> ");

        // Adding decision variable attName
        for (int i = 0; i < deciGroup.attValues.size(); i++) 
        {
            if (deciGroup.attValues.get(i) != null) 
            {
                strbuild.append(this.attrName.get(i));
                strbuild.append(" - ");
                strbuild.append(this.deciGroup.attValues.get(i));
                break;
            }
        }
        strbuild.append(String.format(" | %d | %.2f %% |", this.suppo, this.confi * 100));

        return strbuild.toString();
    }
    
    @Override
    public String toString() 
    {
        // (b1, a5) -> c6 -- Support:2 -- Confidence 50%

        StringBuilder strbuild = new StringBuilder();
        strbuild.append("(");
        for (int j = 0; j < this.attrGroup.attValues.size(); j++) 
        {
            String cVal = this.attrGroup.attValues.get(j);
            String cName = this.attrName.get(j);
            if (cVal != null) 
            {
                strbuild.append(cName);
                strbuild.append(" - ");
                strbuild.append(cVal);
                strbuild.append(", ");
            }
        }

        // Removing comma and space at the end
        strbuild.delete(strbuild.length() - 2, strbuild.length() - 1);

        strbuild.append(")");
        strbuild.append(" --> ");

        // Adding decision variable attName
        for (int j = 0; j < deciGroup.attValues.size(); j++) 
        {
            if (deciGroup.attValues.get(j) != null) 
            {
                strbuild.append(this.attrName.get(j));
                strbuild.append(" - ");
                strbuild.append(this.deciGroup.attValues.get(j));
                break;
            }
        }
        
        strbuild.append(String.format(" #### SUPPORT: %d #### CONFIDENCE: %.2f percent", this.suppo, this.confi * 100));

        return strbuild.toString();
    }
}
