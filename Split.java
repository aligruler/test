package alg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;



public class Split {
	private static final int SPLIT_TRYS = 10;
	private static final int SPLIT_COMBINATIONS = 4;

	public static List<Node> getNodes(Route route) {
		List<Node> nodes = new LinkedList<Node>();
		for (Edge e : route.getEdges()) {
			Node end = e.getEnd();
			nodes.add(e.getEnd());
		}
		return nodes;
	}

	public static List<Node> getAllNodes(Route route) {
		List<Node> nodes = new LinkedList<Node>();
		for (Edge e : route.getEdges()) {
			nodes.add(e.getEnd());
		}
		return nodes;
	}

	public static List<Node> getNodes(List<Route> routes) {
		List<Node> nodes = new LinkedList<Node>();
		for (Route route : routes) {
			for (Edge e : route.getEdges()) {
				// System.out.printf(e.getOrigin().getId()+" ->
				// "+e.getEnd().getId()+" ");
				if (e.getEnd().getId() != 0) {
					Node end = e.getEnd();
					nodes.add(e.getEnd());
				}
			}
		}
		return nodes;
	}

	public static int countNodes(Solution sol) {
		HashSet<Integer> nodsCount = new HashSet<Integer>();
		for (Route r : sol.getRoutes()) {
			for (Edge e : r.getEdges()) {
				Node n = e.getEnd();
				if(n.getId()!= 0 && nodsCount.contains(n.getId())){
					//System.out.println("Duplicado "+n.getId());
				}
				nodsCount.add(n.getId());
			}
		}
		return nodsCount.size();
	}

	public static int countNodes(Route r) {
		HashSet<Integer> nodsCount = new HashSet<Integer>();

		for (Edge e : r.getEdges()) {
			nodsCount.add(e.getEnd().getId());
		}

		return nodsCount.size();
	}

	
	public static String plotNodeVector(List<Node> nodes){
		String res = "";
		Collections.sort(nodes, new Comparator<Node>(){

			@Override
			public int compare(Node o1, Node o2) {
				// TODO Auto-generated method stub
				return o1.getId()-o2.getId();
			}
			
		});
		for(Node n: nodes) {
			res += n.getId()+",";
		}
		return res;
	}
	public static Solution splitSol(Solution newSolDet, Test aTest, Inputs inp, Random rng) {
		int nRoutes = newSolDet.getRoutes().size();	
		
		Boolean mejoro = false;
		Solution sol = null;
		

		if (nRoutes > 2) {
			for (int j = 0; j < SPLIT_COMBINATIONS; j++) {
				int routesToSplit = 2 + rng.nextInt(nRoutes - 2) / 2;
				List<Route> routes = new LinkedList<Route>(newSolDet.getRoutes());
				List<Route> routesSplit = new LinkedList<Route>();
				double bestCost = 0;
				for (int i = 0; i < routesToSplit; i++) {
					// System.out.println(routes.size());
					// System.out.println(routes.size());
					int p = rng.nextInt(routes.size());
					Route routeToAdd = routes.remove(p);
					routesSplit.add(routeToAdd);
					bestCost += routeToAdd.getCosts();
				}
				
				List<Node> nodes = getNodes(routesSplit);
				//nodes.addAll(newSolDet.getNonServedNodes());
				nodes.add(0, inp.getNodes()[0]);

				//System.out.println("Solving for: "+plotNodeVector(nodes));
				List<Edge> edges = InputsManager.generateSavingsList(nodes);

				int edgeSize = edges.size();
				Solution bestSubSol = null;
				
				if (nodes.get(0) != edges.get(0).getEnd().getInRoute().getEdges().get(0).getOrigin()){
				//	System.out.println("Para algo paso!!");
				}
				
				for (int i = 0; i < SPLIT_TRYS; i++) {

					Solution subSol = RandCWS.solve(aTest, edges, nodes, rng, true, inp.getVehCap());
					if (subSol.getRoutingCosts() < bestCost) {
						 bestSubSol = subSol;
						 bestCost = subSol.getRoutingCosts();
						// System.out.println("Encontro mejor ruta en split!");
					}
					
				}

				if (bestSubSol != null) {
					
					Solution auxSolDet = new Solution(bestSubSol.getNonServedNodes());
					float accDemand = 0;
					double stockCosts = 0;

					double cost = 0;
					for (Route r : routes) {
						auxSolDet.addRoute(r);
						cost += r.getCosts();
						
					}

					for (Route r : bestSubSol.getRoutes()) {
						auxSolDet.addRoute(r);
						cost += r.getCosts();
						
					}
					
						
					auxSolDet.setRoutingCosts(cost);

					for (int i = 1; i < inp.getNodes().length; i++) // node 0 is depot
					{
						Node aNode = inp.getNodes()[i];		
						accDemand = accDemand + aNode.getActivePolicy().getUnitsToServe();
						stockCosts = stockCosts + aNode.getActivePolicy().getExpStockCosts();
					}

					auxSolDet.setDemandToServe(accDemand);
					auxSolDet.setStockCosts(stockCosts);
					auxSolDet.setTotalCosts(stockCosts + auxSolDet.getRoutingCosts());
					return auxSolDet;
					
					/*
					auxSolDet.setRoutingCosts(cost);
					
					auxSolDet.setDemandToServe(newSolDet.getDemandToServe());
					auxSolDet.setStockCosts(newSolDet.getStockCosts());
					auxSolDet.setTotalCosts(auxSolDet.getStockCosts() + auxSolDet.getRoutingCosts());	

					//System.out.println("sssss demand " + auxSolDet.getDemandToServe());
					
					for (int i = 1; i < inp.getNodes().length; i++) // node 0 is depot							
					{
						Node aNode = inp.getNodes()[i];
						//Policy index = aNode.getBestSolPolicy();
						int newPolicy = rng.nextInt(inp.getNodes()[i].getPoliciesByRefill().length-1);
						Policy index = aNode.getRandomPolicy(newPolicy);
						
						
						
						Policy auxActivePolicy = aNode.getActivePolicy(); // auxiliar copy
						aNode.setActivePolicy(index);
						
						
					    sol = new Solution(RandCWS.solve(aTest, inp, rng, true));
					    int factible = checkCap(sol , inp.getVehCap());
						if(factible == 1){
							aNode.setActivePolicy(auxActivePolicy);
							
						}
						else{
							accDemand = auxSolDet.getDemandToServe() + index.getUnitsToServe() - auxActivePolicy.getUnitsToServe();
							stockCosts = auxSolDet.getStockCosts() + index.getExpStockCosts()- auxActivePolicy.getExpStockCosts();
							
							
							if(auxSolDet.getTotalCosts()  > (sol.getRoutingCosts() + stockCosts) )
							{
								sol.setDemandToServe(accDemand);
								sol.setStockCosts(stockCosts);
								sol.setTotalCosts(sol.getStockCosts() + sol.getRoutingCosts());	
								//System.out.println("nueva sol " + sol.getTotalCosts());
								auxSolDet = new Solution(sol); 
							}else{
								aNode.setActivePolicy(auxActivePolicy);
							}

						}
					}

					*/	

			
					/*System.out.println("HACE SPLITTTTTTTTT!!!!!");
					
					for(int i = 0; i<1000;i++){
		        		Solution sol = new Solution(RandCWS.solve(aTest, inp, rng, false));

						if(sol.getRoutingCosts() != auxSolDet.getRoutingCosts()){
		        			System.err.println(sol.getRoutingCosts() +" "+ auxSolDet.getRoutingCosts());
		        		}
					}*/
					
				
					
				}

			}
		}

		//System.out.println("NOOOO HAce SPLITTTTTTTTT!!!!!");
		return newSolDet;
	}
	
	
    private static int checkCap(Solution auxSolDet , float capacity){   
	     for(int i = 0; i < auxSolDet.getRoutes().size();i++){
			 double  totservedIS = 0;
			 for(int jj =0; jj < auxSolDet.getRoutes().get(i).getEdges().size(); jj++ ){
				 Edge tE = auxSolDet.getRoutes().get(i).getEdges().get(jj);
				 
				 if(tE.getEnd().getId() != 0 ){
					 totservedIS += tE.getEnd().getUnitsToServe();
				 }
				 if(totservedIS > capacity){
      		       return 1;
				 }
			 
			 }
	     }
	    return 0;
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