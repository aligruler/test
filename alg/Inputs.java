package alg;
import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Inputs implements Serializable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private Node[] nodes; // List of all nodes in the problem/sub-problem
    private float vCap = 0.0F; // Vehicle capacity (homogeneous fleet)
    private LinkedList<Edge> savings = null; 
    private float[] vrpCenter; // (x-bar, y-bar) is a geometric VRP center
    private double alpha = 0.0;
    private double beta = 0.0;
    
    public Inputs(int n)
    {   nodes = new Node[n]; // n nodes, including the depot
        vrpCenter = new float[2];
    }

    /* GET METHODS */
    public Node[] getNodes(){return nodes;}
    public LinkedList<Edge> getSavings(){return savings;}
    public float getVehCap(){return vCap;}
    public float[] getVrpCenter(){return vrpCenter;}

    /* SET METHODS */
    public void setVrpCenter(float[] center){vrpCenter = center;}
    public void setVehCap(float c){vCap = c;}
    public void setList(LinkedList<Edge> sList){savings = sList;}

    
    public void showPolicies(Inputs input){
    	for (int k = 1; k < input.getNodes().length; k++){
    		System.out.print("Node:" + input.getNodes()[k].getId());
    		input.getNodes()[k].getPoliciesByRefill();
    		System.out.println(" Policy:" + input.getNodes()[k].getActivePolicy().getRefillUpToPercent());
    	}
    }

	
}