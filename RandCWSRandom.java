package alg;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;



public class RandCWSRandom {

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

	

	public static Solution CSWRand(Solution newSolDet, Test aTest, Inputs inp, Random rng) {
		int nRoutes = newSolDet.getRoutes().size();
		

		if (nRoutes > 2) {
		
				List<Route> routes = new LinkedList<Route>(newSolDet.getRoutes());
				//List<Node> nodes = getNodes(routes);
				
				List<Node> nodes = new LinkedList<Node>();

				//System.out.println("Solving for: "+plotNodeVector(nodes));
				//List<Edge> edges = InputsManager.generateSavingsList(nodes);
				List<Edge> edges = inp.getSavings();
				
				for(int i=0;i<inp.getNodes().length;i++){
					nodes.add(inp.getNodes()[i]); 
				}
						
				Solution subSol = RandCWS.solve(aTest, edges, nodes, rng, true, inp.getVehCap());	
				return subSol;
			}
		
		return newSolDet;
	}	

}