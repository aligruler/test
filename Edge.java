package alg;
import java.io.Serializable;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Edge implements Comparable<Edge>, Serializable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private Node origin; // origin node
    private Node end; // end node
    private double costs = 0.0; // edge costs
    private double savings = 0.0; // edge savings (Clarke & Wright)
    private Route inRoute = null; // route containing this edge (0 if no route assigned)
    private Edge inverseEdge = null; // edge with inverse direction
            
    public Edge(Node originNode, Node endNode) 
    {   origin = originNode;
        end = endNode;
    }

    /* SET METHODS */
    public void setCosts(double c){costs = c;}
    public void setSavings(double s){savings = s;}
    public void setInRoute(Route r){inRoute = r;}
    public void setInverse(Edge e){inverseEdge = e;}

    /* GET METHODS */
    public Node getOrigin(){return origin;}
    public Node getEnd(){return end;}
    public double getCosts(){return costs;}
    public double getSavings(){return savings;}
    public Route getInRoute(){return inRoute;}
    public Edge getInverseEdge(){return inverseEdge;}

    /* AUXILIARY METHODS */
    
    public double calcCosts(Node origin, Node end)
    {   double X1 = origin.getX();
        double Y1 = origin.getY();
        double X2 = end.getX();
        double Y2 = end.getY();
        double d = Math.sqrt((X2 - X1) * (X2 - X1) + (Y2 - Y1) * (Y2 - Y1));
        return d;
    }

    public double calcSavings(Node origin, Node end, Node depot)
    {   double X1 = origin.getX();
        double Y1 = origin.getY();
        double X2 = end.getX();
        double Y2 = end.getY();
        double Xd = depot.getX();
        double Yd = depot.getY();
        // Costs of originNode to depot
        double odC = Math.sqrt((Xd - X1)*(Xd - X1) + (Yd - Y1)*(Yd - Y1));
        // Costs of depot-(0, 0) to endNode
        double deC = Math.sqrt((X2 - Xd)*(X2 - Xd) + (Y2 - Yd)*(Y2 - Yd));
        // Costs of originNode to endNode
        double oeC = Math.sqrt((X2 - X1)*(X2 - X1) + (Y2 - Y1)*(Y2 - Y1));
        //Return cost depot to savings
        return odC + deC - oeC;
    }
    
  
    public int compareTo(Edge otherEdge) 
    {   Edge other = otherEdge;
        double s1 = this.getSavings();
        double s2 = other.getSavings();
       
        if( s1 < s2 ){
            return -1;
        }else if (s1 > s2){
            return 1;
        }else{
        	return 0;
        }
    }
    
    @Override
    public String toString() 
    {   String s = "";
        s = s.concat("\nEdge origin: " + this.getOrigin());
        s = s.concat("\nEdge end: " + this.getEnd());
        s = s.concat("\nEdge costs: " + (this.getCosts()));
        s = s.concat("\nEdge savings: " + (this.getSavings()));
        return s;
    }
}