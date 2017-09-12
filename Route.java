package alg;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Route implements Serializable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private double costs = 0.0; // route total costs
    private float demand = 0.0F; // route total demand
    private LinkedList<Edge> edges; // edges list
    private float[] center; // (x-bar, y-bar) for all (x,y) in the route
    private float getYRouteCenter = 0.0F; 
    private float getXRouteCenter = 0.0F;
    
    public float getYRouteCenter() {
		return getYRouteCenter;
	}

	public void setYRouteCenter(float getYRouteCenter) {
		this.getYRouteCenter = getYRouteCenter;
	}

	public float getXRouteCenter() {
		return getXRouteCenter;
	}

	public void setXRouteCenter(float getXRouteCenter) {
		this.getXRouteCenter = getXRouteCenter;
	}

	public Route() 
    {   edges = new LinkedList<Edge>();
        center = new float[2];
    }
    
    /* SET METHODS */
    public void setCosts(double c){costs = c;}
    public void setDemand(float d){demand = d;}
    public void setCenter(float[] coord){center = coord;}
    public void setEdges(LinkedList<Edge> e){edges = e;}

    /* LIST GET METHODS*/
    public double getCosts(){return costs;}
    public float getDemand(){return demand;}
    public float[] getCenter(){return center;}
    public List<Edge> getEdges(){return edges;}
    
    /* AUXILIARY METHODS */

    /** 
     * Reverses a route, e.g. (0 -> 2 -> 6 -> 0) becomes (0 -> 6 -> 2 -> 0)
     */
    public void reverse()
    {   
        for( int i = 0; i < edges.size(); i++ )
        {   Edge e = edges.get(i);
            Edge invE = e.getInverseEdge();
            edges.remove(e);
            edges.add(0, invE);
        }
    }
    
    @Override
    public String toString() 
    {   String s = "";
        s = s.concat("\nRute costs: " + (this.getCosts()));
        s = s.concat("\nRuta demand:" + this.getDemand());
        s = s.concat("\nRuta edges: " + this.getEdges());
        return s;
    }
}