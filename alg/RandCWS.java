package alg;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Biased-randomized version of the Clarke & Wright savings (CWS) heuristic.
 * 
 * @author Angel A. Juan - ajuanp(@)gmail.com
 * @version 130807
 */
public class RandCWS {
	/**
	 * Generates a biased-randomized solution based on the CWS heuristic.
	 * 
	 * @param aTest
	 * @param inputs
	 * @param useRandom
	 * @return CWS sol if useRandom = false; biased-randomized sol otherwise.
	 */
	public static Solution solve(Test aTest, Inputs inputs, Random rng, boolean useRandom) {
		/* 1. RESET VARIABLES */
		// dummySol resets isInterior and inRoute in nodes
		Solution currentSol = generateDummySol(inputs);
		Node depot = inputs.getNodes()[0];
		int index;
		double alpha = aTest.getBeta1();
		double beta = aTest.getBeta2();
		
		/* 2. MAKE A COPY OF THE SAVINGS LIST */
		List<Edge> savings = new LinkedList<Edge>();
		for (Edge e : inputs.getSavings())
			// only add edges connecting points to be served.
			if (e.getOrigin().getUnitsToServe() > 0 && e.getEnd().getUnitsToServe() > 0)
				savings.add(0, e); // Copy the savingsList in reverse order

		/* 3. PERFORM THE EDGE-SELECTION & ROUTING-MERGING ITERATIVE PROCESS */
		while (savings.isEmpty() == false) {
			// 3.1. Select the next edge from the list (either at random or not)
			if (useRandom == false) // classical Clarke & Wright solution
				index = 0; // greedy behavior
			else // suffle the savingsList
				index = getRandomPosition(alpha,beta, rng, savings.size());

			Edge ijEdge = savings.get(index);
			savings.remove(ijEdge); // remove edge from list

			// 3.2. Determine the nodes i < j that define the edge
			Node iNode = ijEdge.getOrigin();
			Node jNode = ijEdge.getEnd();

			// 3.3. Determine the routes associated with each node
			Route iR = iNode.getInRoute();
			Route jR = jNode.getInRoute();

			// 3.4. If all necessary conditions are satisfied, merge
			boolean isMergePossible = false;
			isMergePossible = checkMergingConditions(aTest, inputs, iR, jR, ijEdge);
			if (isMergePossible == true) { // 3.4.1. Get an edge iE in iR
											// containing nodes i and 0
				Edge iE = getEdge(iR, iNode, depot); // iE is either (0,i) or
														// (i,0)
				// 3.4.2. Remove edge iE from iR route and update costs
				iR.getEdges().remove(iE);
				iR.setCosts(iR.getCosts() - iE.getCosts());
				// 3.4.3. If there are more than one edge then i will be
				// interior
				if (iR.getEdges().size() > 1)
					iNode.setIsInterior(true);
				// 3.4.4. If new route iR does not start at 0 it must be
				// reversed
				if (iR.getEdges().get(0).getOrigin().getId() != depot.getId() )
					iR.reverse();
				// 3.4.5. Get an edge jE in jR containing nodes j and 0
				Edge jE = getEdge(jR, jNode, depot); // jE is either (0,j) or
														// (j,0)
				// 3.4.6. Remove edge jE from jR route
				jR.getEdges().remove(jE);
				jR.setCosts(jR.getCosts() - jE.getCosts());
				// 3.4.7. If there are more than one edge then j will be
				// interior
				if (jR.getEdges().size() > 1)
					jNode.setIsInterior(true);
				// 3.4.8. If new route jR starts at 0 it must be reversed
				if (jR.getEdges().get(0).getOrigin().getId()  == depot.getId() )
					jR.reverse(); // reverseRoute(inputs, jR);
				// 3.4.9. Add ijEdge = (i, j) to new route iR
				iR.getEdges().add(ijEdge);
				iR.setCosts(iR.getCosts() + ijEdge.getCosts());
				iR.setDemand(iR.getDemand() + ijEdge.getEnd().getUnitsToServe());
				jNode.setInRoute(iR);
				// 3.4.10. Add route jR to new route iR
				for (Edge e : jR.getEdges()) {
					iR.getEdges().add(e);
					iR.setDemand(iR.getDemand() + e.getEnd().getUnitsToServe());
					iR.setCosts(iR.getCosts() + e.getCosts());
					e.getEnd().setInRoute(iR);
				}
				// 3.4.11. Delete route jR from currentSolution
				currentSol.setRoutingCosts(currentSol.getRoutingCosts() - ijEdge.getSavings());
				currentSol.getRoutes().remove(jR);
			}
	
		}
		/* 4. RETURN THE SOLUTION */
		
		
		//System.out.println("NO SPLIT CWS CWS Served:"+plotNodeVector(getNodes(currentSol.getRoutes())));
		//System.out.println("NO SPLIT XXCWS CWS Nonserved:"+plotNodeVector(currentSol.getNonServedNodes()));
		
		
		
		
		return currentSol;
	}

	public static Solution solve(Test aTest, List<Edge> edges, List<Node> nodes, Random rng, boolean useRandom,
			float Vcapacity) {
		/* 1. RESET VARIABLES */
		// dummySol resets isInterior and inRoute in nodes
		Solution currentSol = generateDummySol(nodes);
		
		if(currentSol.getId() == 716){
			//System.out.println("Paraaaaaaa 716");
			//System.out.println("DUMMY XXCWS CWS "+ currentSol.getId() + "Served:"+plotNodeVector(getNodes(currentSol.getRoutes())));
			//System.out.println("DUMMY XXCWS IN CWS Nonserved:"+plotNodeVector(currentSol.getNonServedNodes()));
		}
		
		Node depot = nodes.get(0);
		int index;
		double beta = aTest.getBeta2();
		double alpha = aTest.getBeta1();

		/* 2. MAKE A COPY OF THE SAVINGS LIST */
		List<Edge> savings = new LinkedList<Edge>();
		for (Edge e : edges)
			// only add edges connecting points to be served.
			if (e.getOrigin().getUnitsToServe() > 0 && e.getEnd().getUnitsToServe() > 0)
				savings.add(0, e); // Copy the savingsList in reverse order

		/* 3. PERFORM THE EDGE-SELECTION & ROUTING-MERGING ITERATIVE PROCESS */
		while (savings.isEmpty() == false) {
			// 3.1. Select the next edge from the list (either at random or not)
			if (useRandom == false) // classical Clarke & Wright solution
				index = 0; // greedy behavior
			else // suffle the savingsList
				index = getRandomPositionalpha(alpha, beta, rng, savings.size());

			Edge ijEdge = savings.get(index);
			savings.remove(ijEdge); // remove edge from list

			// 3.2. Determine the nodes i < j that define the edge
			Node iNode = ijEdge.getOrigin();
			Node jNode = ijEdge.getEnd();

			// 3.3. Determine the routes associated with each node
			Route iR = iNode.getInRoute();
			Route jR = jNode.getInRoute();

			// 3.4. If all necessary conditions are satisfied, merge
			boolean isMergePossible = false;
			isMergePossible = checkMergingConditions(aTest, Vcapacity, iR, jR, ijEdge);
			if (isMergePossible == true) { // 3.4.1. Get an edge iE in iR
											// containing nodes i and 0
				
				Edge iE = getEdge(iR, iNode, depot); // iE is either (0,i) or
														// (i,0)
				// 3.4.2. Remove edge iE from iR route and update costs
				iR.getEdges().remove(iE);
				iR.setCosts(iR.getCosts() - iE.getCosts());
				// 3.4.3. If there are more than one edge then i will be
				// interior
				if (iR.getEdges().size() > 1)
					iNode.setIsInterior(true);
				// 3.4.4. If new route iR does not start at 0 it must be
				// reversed
				if (iR.getEdges().get(0).getOrigin().getId() != depot.getId())
					iR.reverse();
				// 3.4.5. Get an edge jE in jR containing nodes j and 0
				Edge jE = getEdge(jR, jNode, depot); // jE is either (0,j) or
														// (j,0)
				// 3.4.6. Remove edge jE from jR route
				jR.getEdges().remove(jE);
				jR.setCosts(jR.getCosts() - jE.getCosts());
				// 3.4.7. If there are more than one edge then j will be
				// interior
				if (jR.getEdges().size() > 1)
					jNode.setIsInterior(true);
				// 3.4.8. If new route jR starts at 0 it must be reversed
				if (jR.getEdges().get(0).getOrigin().getId() == depot.getId())
					jR.reverse(); // reverseRoute(inputs, jR);
				// 3.4.9. Add ijEdge = (i, j) to new route iR
				iR.getEdges().add(ijEdge);
				iR.setCosts(iR.getCosts() + ijEdge.getCosts());
				iR.setDemand(iR.getDemand() + ijEdge.getEnd().getUnitsToServe());
				jNode.setInRoute(iR);
				// 3.4.10. Add route jR to new route iR
				for (Edge e : jR.getEdges()) {
					iR.getEdges().add(e);
					iR.setDemand(iR.getDemand() + e.getEnd().getUnitsToServe());
					iR.setCosts(iR.getCosts() + e.getCosts());
					e.getEnd().setInRoute(iR);
				}
				// 3.4.11. Delete route jR from currentSolution
				currentSol.setRoutingCosts(currentSol.getRoutingCosts() - ijEdge.getSavings());
				currentSol.getRoutes().remove(jR);

			}
			//System.out.println("XXCWS CWS "+ currentSol.getId() + "Served:"+plotNodeVector(getNodes(currentSol.getRoutes())));
			//System.out.println("XXCWS IN CWS Nonserved:"+plotNodeVector(currentSol.getNonServedNodes()));
		}
		/* 4. RETURN THE SOLUTION */
		//System.out.println("final "+currentSol.getRoutes().size()+"("+Split.countNodes(currentSol)+")"+"//"+currentSol.getNonServedNodes().size());
		
		//System.out.println("XXCWS CWS "+ currentSol.getId() + "Served:"+plotNodeVector(getNodes(currentSol.getRoutes())));
		//System.out.println("XXCWS CWS Nonserved:"+plotNodeVector(currentSol.getNonServedNodes()));
		
		
		return currentSol;
	}

	/**
	 * Constructs an initial dummy feasible solution as described in the CWS
	 * heuristic: dummySol = { (0,i,0) / i in vrpNodesList } During this
	 * process, inRoute and isInterior values are assigned.
	 */
	private static Solution generateDummySol(Inputs inputs) {
		Solution sol = new Solution();
		//float total = 0.0f;
		for (int i = 1; i < inputs.getNodes().length; i++) // i = 0 is the depot
		{
			Node iNode = inputs.getNodes()[i];
			// Consider only nodes with positive demand.
			
			if (iNode.getUnitsToServe() > 0) {
				// Get diEdge and idEdge
				//total += iNode.getUnitsToServe();
				
				Edge diEdge = iNode.getDiEdge();
				Edge idEdge = iNode.getIdEdge();
				// Create didRoute (and set corresponding total costs and
				// demand)
				Route didRoute = new Route();
				didRoute.getEdges().add(diEdge);
				didRoute.setDemand(didRoute.getDemand() + diEdge.getEnd().getUnitsToServe());
				didRoute.setCosts(didRoute.getCosts() + diEdge.getCosts());
				didRoute.getEdges().add(idEdge);
				didRoute.setCosts(didRoute.getCosts() + idEdge.getCosts());
				// Update iNode properties (inRoute and isInterior)
				iNode.setInRoute(didRoute); // save route to which node belongs
				iNode.setIsInterior(false); // node is directly connected to
											// depot
				// Add didRoute to current solution
				sol.getRoutes().add(didRoute);
				sol.setRoutingCosts(sol.getRoutingCosts() + didRoute.getCosts());
				sol.setServedDemand(sol.getServedDemand() + didRoute.getDemand());
			} else if (iNode.getId() != 0){
				sol.getNonServedNodes().add(iNode);
			}	
		}
		//System.err.println("TOTAL DEMAND" + total);
		return sol;
	}

	private static Solution generateDummySol(List<Node> nodes) {
		Solution sol = new Solution();
		for (int i = 1; i < nodes.size(); i++) // i = 0 is the depot
		{
			Node iNode = nodes.get(i);
			// Consider only nodes with positive demand.
			if (iNode.getUnitsToServe() > 0) {
				// Get diEdge and idEdge
				Edge diEdge = iNode.getDiEdge();
				Edge idEdge = iNode.getIdEdge();
				// Create didRoute (and set corresponding total costs and
				// demand)
				Route didRoute = new Route();
				didRoute.getEdges().add(diEdge);
				didRoute.setDemand(didRoute.getDemand() + diEdge.getEnd().getUnitsToServe());
				didRoute.setCosts(didRoute.getCosts() + diEdge.getCosts());
				didRoute.getEdges().add(idEdge);
				didRoute.setCosts(didRoute.getCosts() + idEdge.getCosts());
				// Update iNode properties (inRoute and isInterior)
				iNode.setInRoute(didRoute); // save route to which node belongs
				iNode.setIsInterior(false); // node is directly connected to
											// depot
				// Add didRoute to current solution
				sol.getRoutes().add(didRoute);
				sol.setRoutingCosts(sol.getRoutingCosts() + didRoute.getCosts());
				sol.setServedDemand(sol.getServedDemand() + didRoute.getDemand());
			} else if (iNode.getId() != 0)
				sol.getNonServedNodes().add(iNode);
		}
		if (sol.getNonServedNodes().size() > 0){
			//System.out.println("NON SERVED:" + sol.getNonServedNodes().size());
			
		}
		//System.out.println("RESULTING ROUTES:"+sol.getRoutes().size()+" for "+nodes.size());
		return sol;
	}

	/**
	 * Given aRoute, iNode and depot, returns the edge in aRoute which contains
	 * iNode and depot (it will be the first of the last edge)
	 */
	private static Edge getEdge(Route aRoute, Node iNode, Node depot) {
		// Check if firstEdge in aRoute contains iNode and depot
		Edge firstEdge = aRoute.getEdges().get(0);
		Node origin = firstEdge.getOrigin();
		Node end = firstEdge.getEnd();
		

			
		if ((origin.getId() == iNode.getId() && end.getId() == depot.getId()) || (origin.getId() == depot.getId() && end.getId() == iNode.getId()))
			return firstEdge;
		else {
			int lastIndex = aRoute.getEdges().size() - 1;
			Edge lastEdge = aRoute.getEdges().get(lastIndex);
			return lastEdge;
		}
	}

	private static boolean checkMergingConditions(Test aTest, Inputs inputs, Route iR, Route jR, Edge ijEdge) {
		// Condition 1: iR and jR are not the same route
		if (iR == jR)
			return false;
		// Condition 2: both nodes are exterior nodes in their respective routes
		Node iNode = ijEdge.getOrigin();
		Node jNode = ijEdge.getEnd();
		if (iNode.getIsInterior() == true || jNode.getIsInterior() == true)
			return false;
		// Condition 3: demand after merging can be covered by a single vehicle
		if (inputs.getVehCap() < iR.getDemand() + jR.getDemand())
			return false;
		// Condition 4: total costs (distance) after merging are feasible
		float maxRoute = aTest.getMaxRouteCosts();
		float serviceCosts = aTest.getServiceCosts();
		int nodesInIR = iR.getEdges().size();
		int nodesInJR = jR.getEdges().size();
		double newCost = iR.getCosts() + jR.getCosts() - ijEdge.getSavings();
		if (newCost > maxRoute - serviceCosts * (nodesInIR + nodesInJR - 2))
			return false;

		return true;
	}

	private static boolean checkMergingConditions(Test aTest, float Vcapacity, Route iR, Route jR, Edge ijEdge) {
		// Condition 1: iR and jR are not the same route
		if (iR == jR)
			return false;
		// Condition 2: both nodes are exterior nodes in their respective routes
		Node iNode = ijEdge.getOrigin();
		Node jNode = ijEdge.getEnd();
		if (iNode.getIsInterior() == true || jNode.getIsInterior() == true)
			return false;
		// Condition 3: demand after merging can be covered by a single vehicle
		if (Vcapacity < iR.getDemand() + jR.getDemand())
			return false;
		// Condition 4: total costs (distance) after merging are feasible
		float maxRoute = aTest.getMaxRouteCosts();
		float serviceCosts = aTest.getServiceCosts();
		int nodesInIR = iR.getEdges().size();
		int nodesInJR = jR.getEdges().size();
		double newCost = iR.getCosts() + jR.getCosts() - ijEdge.getSavings();
		if (newCost > maxRoute - serviceCosts * (nodesInIR + nodesInJR - 2))
			return false;

		return true;
	}

	private static int getRandomPosition(double alpha, double beta, Random r, int size) {
		double randomValue = alpha + (beta - alpha) * r.nextDouble();
		int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - randomValue));
		// int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - beta));
		index = index % size;
		return index;
	}
	
	
	private static int getRandomPositionalpha(double beta, double alpha, Random r, int size) {
		int index = (int) (Math.log(r.nextDouble()) / Math.log(1 - beta));
		index = index % size;
		return index;
	}
	
	
	////FUnciones de test
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
	
	
	public static List<Node> getNodes(List<Route> routes) {
		List<Node> nodes = new LinkedList<Node>();
		int i = 0;
		for (Route route : routes) {
			System.out.printf("\nNew route %d ",i);
			for (Edge e : route.getEdges()) {
				 System.out.printf(e.getOrigin().getId()+ "->"+e.getEnd().getId()+" ");
				if (e.getEnd().getId() != 0) {
					Node end = e.getEnd();
					nodes.add(e.getEnd());
				}
			}
			i++;
		}
		return nodes;
	}

}