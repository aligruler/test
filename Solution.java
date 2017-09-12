package alg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

/**
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130112
 */
public class Solution implements Cloneable
{
    /* INSTANCE FIELDS & CONSTRUCTOR */
    private static long nInstances = 0; // number of instances
    private long id; // solution ID
    private double stockCosts = 0.0; // solution stock costs
    private double routingCosts = 0.0; // solution routing costs
    private double totalCosts = 0.0; // solution total costs
    private double refinedRoutingCosts = 0.0;
    private double refinedTotCosts = 0.0;
    private float demandToServe = 0.0f; // total demand to serve
    private float servedDemand = 0.0F; // accum. demand served so far
    private LinkedList<Route> routes; // list of routes in this solution
    private LinkedList<Node> nonServedNodes; // list of non-served nodes
    private double time = 0.0; // elapsed computational time (in seconds)
    private double accStockCosts = 0.0; // accumulative solution stock costs
    private double accRoutingCosts = 0.0; //accumulative  solution routing costs
    private double accTotCosts = 0.0;
    private double accRefinedTotCost = 0.0;
    private double detCosts = 0.0;
    private double accDetCosts = 0.0;
    
    


	public Solution()
    {   nInstances++;
        id = nInstances;
        routes = new LinkedList<Route>();
        nonServedNodes = new LinkedList<Node>();
    }
    
    //new constructor
    public Solution(Solution sol)
    {   nInstances++;
        id = nInstances;
        stockCosts = sol.stockCosts; 
        routingCosts = sol.routingCosts; 
        totalCosts = sol.totalCosts;
        demandToServe = sol.demandToServe; 
        servedDemand = sol.servedDemand; 
        accStockCosts = sol.accStockCosts;
        accRoutingCosts = sol.accRoutingCosts;
        accTotCosts = sol.accTotCosts;
        detCosts = sol.detCosts;
        accDetCosts = sol.accDetCosts;
        time = sol.time;
        routes = (LinkedList<Route>)deepClone(sol.getRoutes()); 
        nonServedNodes = (LinkedList<Node>)deepClone(sol.getNonServedNodes());
        
        //routes = new LinkedList<Route>(sol.getRoutes());
        //nonServedNodes = new LinkedList<Node>(sol.getNonServedNodes());
    } 
    
    
    
    

    
    
    public Solution(Solution sol, LinkedList<Route> newroutes)
    {   nInstances++;
        id = nInstances;
        
        stockCosts = sol.stockCosts; 
        routingCosts = sol.routingCosts; 
        totalCosts = sol.totalCosts;
        demandToServe = sol.demandToServe; 
        servedDemand = sol.servedDemand; 
        time = sol.time;
        
        routes = (LinkedList<Route>)deepClone(newroutes); 
        nonServedNodes = (LinkedList<Node>)deepClone(sol.getNonServedNodes());

    } 
    
    
    
    
    
    public double getDetCosts() {
		return detCosts;
	}

	public double getAccDetCosts() {
		return accDetCosts;
	}

	public void setDetCosts(double detCosts) {
		this.detCosts = detCosts;
	}

	public void setAccDetCosts(double accDetCosts) {
		this.accDetCosts = accDetCosts;
	}

	public Solution(LinkedList<Node> nonServed)
    {   nInstances++;
    	id = nInstances;
    	routes = new LinkedList<Route>();
        nonServedNodes = new LinkedList<Node>(nonServed);
    } 
    
    
	public double getRefinedRoutingCosts() {
		return refinedRoutingCosts;
	}

	public void setRefinedRoutingCosts(double refinedRoutingCosts) {
		this.refinedRoutingCosts = refinedRoutingCosts;
	}

	public double getRefinedTotCosts() {
		return refinedTotCosts;
	}

	public void setRefinedTotCosts(double refinedTotCosts) {
		this.refinedTotCosts = refinedTotCosts;
	}	
    

    
   
    /* GET METHODS */
    public LinkedList<Route> getRoutes(){return routes;}
    public long getId(){return id;}
    public double getStockCosts(){return stockCosts;}
    public double getRoutingCosts(){return routingCosts;}
    public double getTotalCosts(){return totalCosts;}
    public float getServedDemand(){return servedDemand;}
    public double getTime(){return time;}
    public float getDemandToServe(){return demandToServe;}
    public LinkedList<Node> getNonServedNodes(){return nonServedNodes;}
    
    

    public void addRoute(Route aRoute, Integer index) {
    	routes.add(index, (aRoute));
   }
    
    public void addRoute(Route aRoute) {
    	routes.add(aRoute);
   }
    
    public void deleteRoute(int i) {
    	routes.remove(i);
   } 
    
    public double getAccRefinedTotCost() {
		return accRefinedTotCost;
	}

	public void setAccRefinedTotCost(double accRefinedTotCost) {
		this.accRefinedTotCost = accRefinedTotCost;
	}


	private boolean improved = false;
    
    
    public boolean isImproved() {
		return improved;
	}

	public void setImproved(boolean improved) {
		this.improved = improved;
	}

	public double getAccTotCosts() {
		return accTotCosts;
	}

	public void setAccTotCosts(double accTotCosts) {
		this.accTotCosts = accTotCosts;
	}

	public double getAccStockCosts() {
		return accStockCosts;
	}

	public void setAccStockCosts(double accStockCosts) {
		this.accStockCosts = accStockCosts;
	}

	public double getAccRoutingCosts() {
		return accRoutingCosts;
	}

	public void setAccRoutingCosts(double accRoutingCosts) {
		this.accRoutingCosts = accRoutingCosts;
	}
    
    
    
    /* SET METHODS */
    public void setStockCosts(double c){stockCosts = c;}
    public void setRoutingCosts(double c){routingCosts = c;}
    public void setTotalCosts(double c){totalCosts = c;}
    public void setServedDemand(float d){servedDemand = d;}
    public void setTime(double t){time = t;}
    public void setDemandToServe(float d){demandToServe = d;}
    
    /*  AUXILIARY METHODS */
    
    @Override
    public String toString()
    {
        Route aRoute; // auxiliary Route variable
        String s = "";
        s = s.concat("\r\n");
        s = s.concat("Sol ID : " + getId() + "\r\n");
        s = s.concat("Sol stock costs: " + getStockCosts() + "\r\n");
        s = s.concat("Sol routing costs: " + getRoutingCosts() + "\r\n");
        s = s.concat("Sol total costs: " + getTotalCosts() + "\r\n");
        s = s.concat("# of routes in sol: " + routes.size());
        s = s.concat("\r\n\r\n\r\n");
        s = s.concat("List of routes (cost and nodes): \r\n\r\n");
        for (int i = 1; i <= routes.size(); i++)
        {   aRoute = routes.get(i - 1);
            s = s.concat("Route " + i + " || ");
            s = s.concat("Total Costs = " + aRoute.getCosts() + " || ");
            s = s.concat("Demand  = " + aRoute.getDemand()+ " || ");
            s = s.concat("\r\n");
        }
        //turn detailed data on/off
/*        s = s.concat("\nDETAILED DATA: ");
        s = s.concat("\nnodeId | X-coord | Y-coord | refill-init | refill-best | toServe-init | "
                + "toServe-best | stockCost-init | stockCost-best");
        s = s.concat("\n0" + " | 0.0   | 0.0   |");
        double servedStockCostsIni = 0.0;
        double servedStockCostsBest = 0.0;
        for( int i = 1; i <= routes.size(); i++ )
        {   aRoute = routes.get(i - 1);    
            for( Edge e : aRoute.getEdges() )
            {   Node aNode = e.getEnd();
                s = s.concat("\n" + aNode.getId() + " | " + aNode.getX() + " | " + aNode.getY() + " | ");
                if( aNode.getId() != 0 )
                {   s = s.concat(aNode.getActivePolicy().getRefillUpToPercent()*100 + "% | " +
                        aNode.getBestSolPolicy().getRefillUpToPercent()*100 + "% | " +
                        aNode.getActivePolicy().getUnitsToServe() + " | " +
                        aNode.getBestSolPolicy().getUnitsToServe() + " | " +
                        aNode.getActivePolicy().getExpStockCosts() + " | " +
                        aNode.getBestSolPolicy().getExpStockCosts());
                    servedStockCostsIni += aNode.getActivePolicy().getExpStockCosts();
                    servedStockCostsBest += aNode.getBestSolPolicy().getExpStockCosts();
                }
            }
        }
        s = s.concat("\nStock costs of served nodes - initSol: " + servedStockCostsIni);
        s = s.concat("\nStock costs of served nodes - bestSol: " + servedStockCostsBest);
        s = s.concat("\n\nNon-served nodes: ");
        s = s.concat("\nnodeId | X-coord | Y-coord | refill-init | refill-best | toServe-init | "
                + "toServe-best | stockCost-init | stockCost-best");
        double nonServedStockCostsIni = 0.0;
        double nonServedStockCostsBest = 0.0;
        for( Node aNode : nonServedNodes )
        {   s = s.concat("\n" + aNode.getId() + " | " + aNode.getX() + " | " + aNode.getY() + " | ");
            s = s.concat(aNode.getActivePolicy().getRefillUpToPercent()*100 + "% | " +
                aNode.getBestSolPolicy().getRefillUpToPercent()*100 + "% | " +
                aNode.getActivePolicy().getUnitsToServe() + " | " +
                aNode.getBestSolPolicy().getUnitsToServe() + " | " +
                aNode.getActivePolicy().getExpStockCosts() + " | " +
                aNode.getBestSolPolicy().getExpStockCosts());
            nonServedStockCostsIni += aNode.getActivePolicy().getExpStockCosts();
            nonServedStockCostsBest += aNode.getBestSolPolicy().getExpStockCosts();
        }
        s = s.concat("\nStock costs of non-served nodes - initSol: " + nonServedStockCostsIni);
        s = s.concat("\nStock costs of non-served nodes - bestSol: " + nonServedStockCostsBest);
        double totalStockCostsIni = servedStockCostsIni + nonServedStockCostsIni;
        double totalStockCostsBest = servedStockCostsBest + nonServedStockCostsBest;
        s = s.concat("\n\nTotal stock costs - initSol: " + totalStockCostsIni);
        s = s.concat("\nTotal stock costs - bestSol: " + totalStockCostsBest);
        s = s.concat("\r\n\r\n");*/
        return s;
    }
    
    
    public static Object deepClone(Object object) {
        try {
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(object);
          ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          ObjectInputStream ois = new ObjectInputStream(bais);
          return ois.readObject();
        }
        catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }	
    
    
    
}

