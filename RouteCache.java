package alg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/********************
 * Simple Route Cache
 * 
 * @author emartinezmasip
 * 
 */

public class RouteCache {
	private final HashMap<String, Route> routes;

	public RouteCache() {
		this.routes = new HashMap<String, Route>();
	}

	public boolean isCached(Route route) {
		return isCached(key(route));
	}

	public void put(Route keyRoute, Route route) {
		put(key(keyRoute), route);
	}

	private void put(String key, Route route) {
		routes.put(key, route);
	}

	public boolean isCached(String key) {
		return routes.containsKey(key);
	}

	public Route get(Route route) {
		return get(key(route));
	}

	public Route get(String key) {
		return routes.get(key);
	}

	public double getCachedCost(Route r) {
		String k = key(r);
		if (isCached(k)) {
			return get(k).getCosts();
		}
		return Double.MAX_VALUE;
	}

	public static String key(Route r) {

		int[] v = new int[r.getEdges().size()];
		int i = 0;
		for (Edge e : r.getEdges()) {
			v[i++] = e.getEnd().getId();
		}
		Arrays.sort(v);
		return Arrays.toString(v);
	}
	
	
	

	public static Solution improveWithCache(Solution newSol, RouteCache cache, Inputs inputs) {		
		int n = newSol.getRoutes().size(); 
		double totalRoutingCost = 0.0;
	
		Solution newRoutingSol = new Solution(newSol);

		for (int i = 0; i < n; i++) {
			Route route = new Route();
			route = (Route)deepClone(newRoutingSol.getRoutes().get(i)); 
			String skey = key(route);
			
			if (!cache.isCached(skey)) {
				cache.put(skey, route);
			} else {
				Route rCached = cache.get(skey);
				if (rCached.getCosts() < route.getCosts()) {
					Boolean checkOk = checkPosibleUpdateRouting(inputs,rCached);
					 //System.err.println("HIT!");
					 if(checkOk){
						 totalRoutingCost =  route.getCosts();
						 newRoutingSol.deleteRoute(i);
						 newRoutingSol.addRoute((Route)deepClone(rCached),i);
						 newRoutingSol.setRoutingCosts(newRoutingSol.getRoutingCosts() - totalRoutingCost + rCached.getCosts());
						 newRoutingSol.setTotalCosts(newRoutingSol.getRoutingCosts() + newRoutingSol.getStockCosts()); 
					 }
					 					
				} else {
					cache.put(skey, route);
				}
			}
		}
		
		return new Solution(newRoutingSol); 
		
	}

	public Solution improve(Solution newSol, Inputs inputs) {
		return improveWithCache(newSol, this, inputs);
	}
	
	
	
	
	
	public static Boolean checkPosibleUpdateRouting(Inputs inputs, Route r){
		Boolean checkPolicy = true;
		
		for (Edge e : r.getEdges()) {
			int nodeId = e.getEnd().getId();
 
			if (nodeId != 0){
				if(e.getEnd().getActivePolicy().getRefillUpToPercent() != inputs.getNodes()[nodeId].getActivePolicy().getRefillUpToPercent()){
					checkPolicy = false;
				}
			}	
		}	

	    

	    return checkPolicy;
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
