package alg;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;


/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130807
 */
public class Node implements Serializable
{
	static int counter = 0;
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private int id; // node ID (depotID = 0)
    private float x; // node x coordinate
    private float y; // node y coordinate
    private float expDemand = 0.0f; // node (expected) demand
    private Route inRoute = null; // route containing the node
    private boolean isInterior = false; // interior node in a route
    private Edge diEdge = null; // edge from depot to node
    private Edge idEdge = null; // edge from node to depot
    private float maxCap = 0.0f; // maximum inventory capacity
    private float currentLevel = 0.0f; // current inventory level
    private int numberOfPolicies = 5; // 0.0, 0.25, 0.50, 0.75, 1.0
    private Policy[] policiesByRefill = null; // array of node's policies by refill type
    private Policy[] policiesByCosts = null; // array of node's policies by stock costs
    private double roundtripToDepotCosts = 0.0; 
    private Policy activePolicy = null;
    private Policy bestSolPolicy = null;
    


    public Node(int nodeId, float nodeX, float nodeY, float nodeDemand)
    { 
     	id = nodeId;
        x = nodeX;
        y = nodeY;
        expDemand = nodeDemand;
        maxCap = 2 * expDemand;
        currentLevel = setCurrentLevel();
        policiesByRefill = new Policy[numberOfPolicies];
        policiesByCosts = new Policy[numberOfPolicies];
        for( int i = 0; i < numberOfPolicies; i++ )
        {
            policiesByRefill[i] = new Policy(maxCap, i * 0.25f, currentLevel);
            policiesByCosts[i] = policiesByRefill[i];
        }
    }

    /* SET METHODS */
    public void setInRoute(Route r){inRoute = r;}
    public void setIsInterior(boolean value){isInterior = value;}
    public void setDiEdge(Edge e){diEdge = e;}
    public void setIdEdge(Edge e){idEdge = e;}
    public void setRoundtripToDepotCosts(double c){roundtripToDepotCosts = c;}
    public void setActivePolicy(Policy p){activePolicy = p;}
    public void setBestSolPolicy(Policy p){bestSolPolicy = p;}

    

    /* GET METHODS */
    public int getId(){return id;}
    public float getX(){return x;}
    public float getY(){return y;}
   // public double getPolicyCost(int index){return PolicyCost[index];}
    public float getExpDemand(){return expDemand;}
    public Route getInRoute(){return inRoute;}
    public boolean getIsInterior(){return isInterior;}
    public Edge getDiEdge(){return diEdge;}
    public Edge getIdEdge(){return idEdge;}
    public float getMaxCap(){return maxCap;}
    public float getCurrentLevel(){return currentLevel;}
    public Policy[] getPoliciesByRefill(){return policiesByRefill;}
    public Policy[] getPoliciesByCosts(){return policiesByCosts;}
    public double getRoundtripToDepotCosts(){return roundtripToDepotCosts;}
    public Policy getActivePolicy(){return activePolicy;}
    public Policy getBestSolPolicy(){return bestSolPolicy;}
    public Policy getRandomPolicy(int index){
    	return policiesByRefill[index];
    	}
    public float getUnitsToServe()
    {   if( this.id == 0 ) // depot with no active policy nor demand
            return 0;
        else
            return activePolicy.getUnitsToServe();
    }

    /* AUXILIARY METHODS */
    

    
    private float setCurrentLevel()
    {
        float level = 0.0f; // default (id is odd and multiple of 3)
        if( id % 2 != 0 && id % 3 != 0 ) // id is odd and not multiple of 3
            level = 0.5f * expDemand;
        else if ( id % 2 == 0 && id % 4 == 0 ) // id is even and multiple of 4
            level = expDemand;
        else if ( id % 2 == 0 && id % 4 != 0 ) // id is even and not multiple of 4
            level = 1.5f * expDemand;
        return level;
    }
    
    
    public void setCurrentLevelNewPeriod(float level)
    {
    	currentLevel = level;
    }
    
    
    public void updateNodes(){ 
    	for( int i = 0; i < numberOfPolicies; i++ )
    	{
    		policiesByRefill[i] = new Policy(maxCap, i * 0.25f, currentLevel);
    		policiesByCosts[i] = policiesByRefill[i];
    	}
    }
     
    
    public int getIndexPolicy(float value){
    	int index;
    	if (value == 0.0) {index = 0;}
    	else if (value == 0.25) {index = 1;}
    	else if (value == 0.50) {index = 2;}
    	else if (value == 0.75) {index = 3;}
    	else if (value == 1.0) {index = 4;}
   		else {index = 5;}
    	return index;
   }

     
    
    @Override
    public String toString() 
    {   String s = "";
        s = s.concat(this.getId() + " ");
        s = s.concat(this.getX() + " ");
        s = s.concat(this.getY() + " ");
        s = s.concat(this.getUnitsToServe() + "");
        s = s.concat(this.getMaxCap() + "");
        s = s.concat(this.getCurrentLevel() + "");
        return s;
    }
    
    
}