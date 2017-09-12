package alg;
import java.io.Serializable;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130807
 */
public class Policy implements Comparable<Policy>, Serializable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private float refillUpToPercent = 0.0f; // 0.0, 0.25, 0.50, 0.75, 1.0
    private float refillUpToUnits = 0.0f; // units to refill up to
    private float unitsToServe = 0.0f; // units to serve during the routing process
    private double expStockCosts = 0.0; // expected stock costs for policy
    private float expSurplus = 0.0f; // expected surplus or stock-out due to this policy
            
    public Policy(){}
    public Policy(float maxCap, float percent, float currentLevel) 
    {   
        refillUpToPercent = percent;
        refillUpToUnits = percent * maxCap;
        unitsToServe = Math.max(0, refillUpToUnits - currentLevel);
    }

    /* SET METHODS */
    public void setExpStockCosts(double c){expStockCosts = c;}
    public void setExpSurplus(float s){expSurplus = s;}
    
    /* GET METHODS */
    public float getRefillUpToPercent(){return refillUpToPercent;}
    public float getRefillUpToUnits(){return refillUpToUnits;}
    public float getUnitsToServe(){return unitsToServe;}
    public double getExpStockCosts(){return expStockCosts;}
    public float getExpSurplus(){return expSurplus;}
    public Policy getACopy()
    {   Policy p = new Policy();
        p.refillUpToPercent = this.refillUpToPercent;
        p.refillUpToUnits = this.refillUpToUnits;
        p.unitsToServe = this.unitsToServe;
        p.expStockCosts = this.expStockCosts;
        p.expSurplus = this.expSurplus;
        return p;
    }
    
    /* AUXILIARY METHODS */
   
    public int compareTo(Policy otherPolicy) 
    {   Policy other = otherPolicy;
        double s1 = this.getExpStockCosts();
        double s2 = other.getExpStockCosts();
        if( s1 < s2 )
            return -1;
        else
            return 1;
    }
    
    @Override
    public String toString() 
    {   String s = "";
        s = s.concat("\nRefillUpToPercent: " + this.getRefillUpToPercent());
        s = s.concat("\nRefillUpToUnits: " + this.getRefillUpToUnits());
        s = s.concat("\nUnitsToServe: " + this.getUnitsToServe());
        s = s.concat("\nExpStockCosts: " + this.getExpStockCosts());
        s = s.concat("\nExpSurplus: " + this.getExpSurplus());
        return s;
    }
}